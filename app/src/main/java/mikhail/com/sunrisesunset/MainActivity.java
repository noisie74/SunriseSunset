package mikhail.com.sunrisesunset;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.location.LocationListener;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.io.IOException;
import java.util.Calendar;

import android.location.Address;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SunriseSunsetCalculator sunriseSunsetCalculator;
    GoogleApiClient mGoogleApiClient;
    android.location.Location mLastLocation;
    String mLatitude, mLongitude;

    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.location)
    TextView mLocation;
    @BindView(R.id.sunrise)
    TextView mSunrise;
    @BindView(R.id.sunset)
    TextView mSunset;
    @BindView(R.id.button_sunset)
    Button buttonSunset;
    @BindView(R.id.frag_container)
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        frameLayout =(FrameLayout) findViewById(R.id.frag_container);

        setSunsetButton();
        connectGoogleApiClient();
        checkForLocationEnabled();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getTodaysDate();
    }

    public void getSunriseSunsetCalculator() {


        mLatitude = String.valueOf(mLastLocation.getLatitude());
        mLongitude = String.valueOf(mLastLocation.getLongitude());
        Location location = new Location(mLatitude, mLongitude);
        sunriseSunsetCalculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());

        Log.d("MainActivity", TimeZone.getDefault().getDisplayName());
    }


    public void getSunsetAndSunrise() {

        String officialSunrise = sunriseSunsetCalculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = sunriseSunsetCalculator.getOfficialSunsetForDate(Calendar.getInstance());
        mSunrise.setText("Sunrise today: " + officialSunrise);
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
        mDate.setText("Today is: " + DateFormat.getLongDateFormat(this).format(calendar.getTime()));

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

                getSunriseSunsetCalculator();
                getSunsetAndSunrise();

//                mLocation.setText("Your location: " + String.valueOf(mLastLocation.getLatitude())
//                        + String.valueOf(mLastLocation.getLongitude()));


                (new GetAddressTask(this)).execute(mLastLocation);
            }
        }
    }


    private class GetAddressTask extends AsyncTask<android.location.Location, Void, String> {

        Context mContext;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPostExecute(String address) {
            // Display the current address in the UI
            mLocation.setText(address);
        }

        @Override
        protected String doInBackground(android.location.Location... params) {

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            android.location.Location mLocation = params[0];

            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            } catch (IOException e1) {
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(mLocation.getLatitude()) + " , " +
                        Double.toString(mLocation.getLongitude()) + " passed to address service";
                Log.e("MainActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                address.getLocality();
                address.getCountryName();

                String addressText = "Your location: "
                        + address.getLocality().toString()
                        + ", "
                        + String.format(address.getAdminArea().substring(0, 2)).toUpperCase()
                        + ", "
                        + address.getCountryName().toString();

                return addressText;

            } else

                return "No address found";
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public void setSunsetButton() {

        buttonSunset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "clicked");
                frameLayout.setVisibility(View.VISIBLE);

                SunsetFragment sunsetFragment = new SunsetFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.frag_container, sunsetFragment, "TAG");
                fragmentTransaction.addToBackStack("TAG");
                fragmentTransaction.commit();


            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
