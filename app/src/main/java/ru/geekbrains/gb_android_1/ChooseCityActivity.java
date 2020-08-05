package ru.geekbrains.gb_android_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

public class ChooseCityActivity extends AppCompatActivity {
    TextView chooseCity;
    Spinner citiesSpinner;
    EditText enterCity;
    Button okEnterCity;
    Button okSpinnerCity;
    String selectedCity;
    final static String dataKey = "dataKey";
    ChooseCityActivityPresenter chooseCityActivityPresenter  = ChooseCityActivityPresenter.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city);
        initViews();
        setDataToSpinnerCities();
        setOnBtnOkEnterCityClickListener();
        setOnEnterCityKeyListener();
        setOnBtnOkSpinnerCityClickListener();
        setOnCitiesSpinnerItemSelectedListener();
        showBackBtn();
    }

    private void initViews() {
        chooseCity = findViewById(R.id.chooseYourCity);
        citiesSpinner = findViewById(R.id.cities);
        enterCity = findViewById(R.id.enterCity);
        okEnterCity = findViewById(R.id.okEnterCity);
        okSpinnerCity = findViewById(R.id.okSpinnerCity);
    }

    private void setOnBtnOkEnterCityClickListener() {
        View.OnClickListener btnOkClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), enterCity.getText(), Toast.LENGTH_SHORT).show();
                if (!enterCity.getText().toString().equals("")) {
                    Intent intent = new Intent(ChooseCityActivity.this, MainActivity.class);
                    String chosenCity = enterCity.getText().toString();
                    intent.putExtra(dataKey, chosenCity);
                    startActivity(intent);
                    // Добавляем новый город в спиннер
                    chooseCityActivityPresenter.addCityToCitiesSpinner(chosenCity, getApplicationContext());
                }
                finish();
            }
        };
        okEnterCity.setOnClickListener(btnOkClickListener);
    }

    private void setOnBtnOkSpinnerCityClickListener(){
        okSpinnerCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCity = citiesSpinner.getSelectedItem().toString();
                Toast.makeText(ChooseCityActivity.this, selectedCity, Toast.LENGTH_SHORT).show();
                // Ставим выбранный город на первое место в коллекции, а следовательно в спиннере:
                chooseCityActivityPresenter.putChosenCityToTopInCitiesSpinner(selectedCity);
                Intent intent = new Intent(ChooseCityActivity.this, MainActivity.class);
                intent.putExtra(dataKey, selectedCity);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setOnEnterCityKeyListener() {
        enterCity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(ChooseCityActivity.this, enterCity.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    private void setDataToSpinnerCities(){
        chooseCityActivityPresenter.takeCitiesListFromResources(getResources());
        chooseCityActivityPresenter.getDataFromCitiesSpinner(this, citiesSpinner);
    }

    private void setOnCitiesSpinnerItemSelectedListener() {
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                List<String> cityList = chooseCityActivityPresenter.getCitiesList();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ваш выбор: " + cityList.get(selectedItemPosition), Toast.LENGTH_SHORT);
                toast.show();
            }
            public void onNothingSelected(AdapterView<?> parent) {
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

   // При нажатии на стрелку назад, возвращает на MainActivity (предыдущую в стеке), закрывая текущую активити и передаем выбранный город:
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            // Передаем верхний в спиннере город в MainActivity:
            intent.putExtra(dataKey, chooseCityActivityPresenter.getCitiesList().get(0));
            startActivity(intent);
            finish();
        }
        return true;
    }

}
