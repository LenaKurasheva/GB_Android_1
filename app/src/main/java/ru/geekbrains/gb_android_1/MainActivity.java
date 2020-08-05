package ru.geekbrains.gb_android_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageButton settingsButton, locationButton, readMoreButton;
    private TextView cityTextView;
    private TextView degrees;
    private TextView windInfoTextView, pressureInfoTextView;
    private static String city = "City";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setOnSettingsBtnOnClick();
        setOnLocationBtnOnClick();
        showChosenCityFromChooseCityActivity();
        showCheckedSwitches();
        setOnReadMoreBtnOnClick();
    }

    private void initViews() {
        settingsButton = findViewById(R.id.settingsBottom);
        locationButton = findViewById(R.id.locationButton);
        cityTextView = findViewById(R.id.city);
        degrees = findViewById(R.id.degrees);
        windInfoTextView = findViewById(R.id.windInfoTextView);
        pressureInfoTextView = findViewById(R.id.pressureInfoTextView);
        readMoreButton = findViewById(R.id.readMoreButton);
    }

    private void setOnSettingsBtnOnClick() {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
            }
        });
    }

    private void setOnLocationBtnOnClick() {
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, ChooseCityActivity.class);
            startActivity(intent);
            finish();
            }
        });
    }

    private void showChosenCityFromChooseCityActivity() {
        String cityName = getIntent().getStringExtra(ChooseCityActivity.dataKey);
        if (cityName != null) {
            cityTextView.setText(cityName);
            city = cityName;
            return;
        }
        cityTextView.setText(city);
    }

    private void showCheckedSwitches(){
        boolean[] settingsSwitchArray = getIntent().getBooleanArrayExtra(SettingsActivity.settingsDataKey);
        if(settingsSwitchArray != null) {
            if (settingsSwitchArray[0]) Toast.makeText(this, "NightMode is on", Toast.LENGTH_SHORT).show();
            if (settingsSwitchArray[1]) windInfoTextView.setVisibility(View.VISIBLE);
            if (settingsSwitchArray[2]) pressureInfoTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setOnReadMoreBtnOnClick() {
        readMoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            String wiki = "https://ru.wikipedia.org/wiki/" + city;
            Uri uri = Uri.parse(wiki);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            }
        });
    }
}