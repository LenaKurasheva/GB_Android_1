package ru.geekbrains.gb_android_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageButton settingsButton;
    private TextView city;
    private TextView degrees;
    private TextView cloudy;
    private RecyclerView dayList;
    private Switch nightMode;
    private ImageView weatherStatus;
    private ConstraintLayout fullscreenConstraint;
    private static boolean isNightOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setOnBtnNight();
    }

    private void initViews() {
        dayList = findViewById(R.id.dayList);
        settingsButton = findViewById(R.id.settingsBottom);
        city = findViewById(R.id.city);
        degrees = findViewById(R.id.degrees);
        cloudy = findViewById(R.id.cloudy);
        nightMode = findViewById(R.id.nightMode);
        weatherStatus = findViewById(R.id.weatherStatus);
        fullscreenConstraint = findViewById(R.id.full_screen_constraintlayout);
    }

    private void setOnBtnNight() {
        nightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNightOn){
                    int backColor = ContextCompat.getColor(getApplicationContext(), R.color.day);
                    int nightModeColor = ContextCompat.getColor(getApplicationContext(), R.color.dark_grey);
                    fullscreenConstraint.setBackgroundColor(backColor);
                    nightMode.setTextColor(nightModeColor);
                    isNightOn = false;
                } else {
                    int backColor = ContextCompat.getColor(getApplicationContext(), R.color.night);
                    int colorAccent = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
                    fullscreenConstraint.setBackgroundColor(backColor);
                    isNightOn = true;
                    nightMode.setTextColor(colorAccent);
                }
            }
        });
    }
}