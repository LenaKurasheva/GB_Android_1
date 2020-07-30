package ru.geekbrains.gb_android_1;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    Switch nightModeSwitch;
    Switch pressureSwitch;
    Switch windSpeedSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initViews();
        setOnNightModeSwitchClickListener();
        setOnWindSpeedSwitchClickListener();
        setOnPressureSwitchClickListener();

    }

    private void initViews() {
        nightModeSwitch = findViewById(R.id.night_mode_switch);
        pressureSwitch = findViewById(R.id.pressure_switch);
        windSpeedSwitch = findViewById(R.id.wind_speed_switch);
    }

    private void setOnNightModeSwitchClickListener(){
        nightModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "nightmode", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnPressureSwitchClickListener(){
        pressureSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "pressure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnWindSpeedSwitchClickListener(){
        windSpeedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "windspeed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
