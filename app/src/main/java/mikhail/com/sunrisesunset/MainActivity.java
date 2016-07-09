package mikhail.com.sunrisesunset;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.location.LocationListener;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SunriseSunsetCalculator sunriseSunsetCalculator;
    GoogleApiClient mGoogleApiClient;
    android.location.Location mLastLocation;
    int MY_LOCATION_REQUEST_CODE = 1;
    public LocationManager mLocationManager;


    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.location)
    TextView mLocation;
    @BindView(R.id.sunrise)
    TextView mSunrise;
    @BindView(R.id.sunset)
    TextView mSunset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        connectGoogleApiClient();
        checkForLocationEnabled();
        getSunriseSunsetCalculator();
        getSunsetandSunrise();
        getTodaysDate();

    }

    public void getSunriseSunsetCalculator() {

        Location location = new Location("39.9522222", "-75.1641667");
        sunriseSunsetCalculator = new SunriseSunsetCalculator(location, "America/New_York");


    }

    public void getSunsetandSunrise() {
        String officialSunrise = sunriseSunsetCalculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = sunriseSunsetCalculator.getOfficialSunsetForDate(Calendar.getInstance());
        mSunrise.setText("Sunrise today: " + officialSunrise.toString());
        mSunset.setText("Sunset today: " + officialSunset);

    }

    public void checkForLocationEnabled() {

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    public void getTodaysDate() {

        final Calendar calendar = Calendar.getInstance();
        mDate.setText("Today is: " + DateFormat.getDateFormat(this).format(calendar.getTime()));

    }

    public void connectGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                .PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLocation.setText("Your location: " + String.valueOf(mLastLocation.getLatitude())
                        + String.valueOf(mLastLocation.getLongitude()));

            }
        }
    }


    @Override

    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
