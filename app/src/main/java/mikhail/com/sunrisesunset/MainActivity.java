package mikhail.com.sunrisesunset;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

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
    }
}
