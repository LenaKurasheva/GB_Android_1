package ru.geekbrains.gb_android_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private Switch nightModeSwitch;
    private Switch pressureSwitch;
    private Switch windSpeedSwitch;
    final static String settingsDataKey = "settingsDataKey";
    SettingsActivityPresenter settingsActivityPresenter = SettingsActivityPresenter.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initViews();
        setCurrentSwitchState();
        setOnNightModeSwitchClickListener();
        setOnWindSpeedSwitchClickListener();
        setOnPressureSwitchClickListener();
        showBackBtn();
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
                Toast.makeText(SettingsActivity.this, "nightmode is in dev", Toast.LENGTH_SHORT).show();
                settingsActivityPresenter.changeNightModeSwitchStatus();
            }
        });
    }

    private void setOnPressureSwitchClickListener(){
        pressureSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "pressure added", Toast.LENGTH_SHORT).show();
                settingsActivityPresenter.changePressureSwitchStatus();
            }
        });
    }

    private void setOnWindSpeedSwitchClickListener(){
        windSpeedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "wind speed added", Toast.LENGTH_SHORT).show();
                settingsActivityPresenter.changeWindSpeedSwitchStatus();
            }
        });
    }

    // Показывает стрелку назад на панели действий
    private void showBackBtn() {
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException exc) {
            exc.printStackTrace();
        }
    }

    // При нажатии на стрелку назад, возвращает на MainActivity (предыдущую в стеке), закрывая текущую активити и передаем выбранные параметры:
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra(settingsDataKey, settingsActivityPresenter.createSettingsSwitchArray());
            startActivity(intent);
            finish();
        }
        return true;
    }

    public void setCurrentSwitchState(){
       boolean[] switchArr =  settingsActivityPresenter.getSettingsArray();
       if(switchArr != null){
           nightModeSwitch.setChecked(switchArr[0]);
           windSpeedSwitch.setChecked(switchArr[1]);
           pressureSwitch.setChecked(switchArr[2]);
       }
    }
}
