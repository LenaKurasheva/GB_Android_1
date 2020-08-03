package ru.geekbrains.gb_android_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView degrees;
    private Switch nightMode;
    protected ConstraintLayout fullscreenConstraint;
    private Button increaseDegreesBtn;
    private final String degreesDataKey = "degreesDataKey";
    MainActivityPresenter mainActivityPresenter = MainActivityPresenter.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setOnBtnNight();
        setOnClickBehaviourToIncreaseDegreesBtn();
        mainActivityPresenter.setCurrentMode(fullscreenConstraint, nightMode, getApplicationContext());
        nightMode.setChecked(mainActivityPresenter.getIsNightOn());
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onDestroy()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onSaveInstanceState()");
        String degreesData = degrees.getText().toString();
        saveInstanceState.putString(degreesDataKey, degreesData);
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Toast.makeText(this, "onRestoreInstanceState", Toast.LENGTH_SHORT).show();
        Log.d("Activity lifecycle", "onRestoreInstanceState()");
        String degreesData = savedInstanceState.getString(degreesDataKey);
        degrees.setText(degreesData);
    }

    private void initViews() {
        degrees = findViewById(R.id.degrees);
        nightMode = findViewById(R.id.nightMode);
        fullscreenConstraint = findViewById(R.id.full_screen_constraintlayout);
        increaseDegreesBtn = findViewById(R.id.increaseDegreesBtn);
    }

    private void setOnBtnNight() {
        nightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityPresenter.changeNightModeStatus();
                mainActivityPresenter.setCurrentMode(fullscreenConstraint, nightMode, getApplicationContext());
            }
        });
    }

    private void setOnClickBehaviourToIncreaseDegreesBtn() {
        increaseDegreesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            int degreesCount = Integer.parseInt(degrees.getText().toString());
            String newValue = String.valueOf(++degreesCount);
            degrees.setText(newValue);
            }
        });
    }
}