package ru.geekbrains.gb_android_1;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseCityActivity extends AppCompatActivity {
    TextView chooseCity;
    Spinner cities;
    EditText enterCity;
    Button okEnterCity;
    Button okSpinnerCity;
    String selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city);
        initViews();
        setDataToSpinnerCities();
        setOnBtnOkEnterCityClickListener();
        setOnEnterCityKeyListener();
        setOnBtnOkSpinnerCityClickListener();
        setOnCitiesItemSelectedListener();
    }

    private void initViews() {
        chooseCity = findViewById(R.id.choose_your_city);
        cities = findViewById(R.id.cities);
        enterCity = findViewById(R.id.enter_city);
        okEnterCity = findViewById(R.id.ok_enter_city);
        okSpinnerCity = findViewById(R.id.ok_spinner_city);
    }

    private void setOnBtnOkEnterCityClickListener() {
        View.OnClickListener btnOkClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), enterCity.getText(), Toast.LENGTH_SHORT).show();
            }
        };
        okEnterCity.setOnClickListener(btnOkClickListener);
    }

    private void setOnBtnOkSpinnerCityClickListener(){
        okSpinnerCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCity = cities.getSelectedItem().toString();
                Toast.makeText(ChooseCityActivity.this, selectedCity, Toast.LENGTH_SHORT).show();
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
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Вызываем адаптер
        cities.setAdapter(adapter);
    }

    private void setOnCitiesItemSelectedListener() {
        cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.cities);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ваш выбор: " + choose[selectedItemPosition], Toast.LENGTH_SHORT);
                toast.show();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


}
