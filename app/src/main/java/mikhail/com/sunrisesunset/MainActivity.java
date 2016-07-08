package mikhail.com.sunrisesunset;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    SunriseSunsetCalculator sunriseSunsetCalculator;

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

    public void checkForLocationEnabled(){

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
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

    public void getTodaysDate(){

        final Calendar calendar = Calendar.getInstance();
        mDate.setText("Today is: " + DateFormat.getDateFormat(this).format(calendar.getTime()));

    }
}
