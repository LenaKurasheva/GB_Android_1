package ru.geekbrains.gb_android_1;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;


public class ChooseCityFragment extends Fragment {

    TextView chooseCity;
    Spinner citiesSpinner;
    EditText enterCity;
    Button okEnterCity;
    Button okSpinnerCity;
    static String currentCity = "";
    final static String dataKey = "dataKey";
    final String myLog = "myLog";
    boolean isLandscape;
    ChooseCityActivityPresenter chooseCityActivityPresenter = ChooseCityActivityPresenter.getInstance();

    static ChooseCityFragment create(CurrentCityContainer container) {
        ChooseCityFragment fragment = new ChooseCityFragment();    // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable("currCity", container);
        fragment.setArguments(args);
        return fragment;
    }

    // Получить текущий город из параметра
    String getCurrentCity() {
        CurrentCityContainer chooseCityContainer = (CurrentCityContainer) (Objects.requireNonNull(getArguments())
                .getSerializable("currCity"));
        try {
            return chooseCityContainer.currCityName;
        } catch (Exception e) {
            return "City";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        checkIsPositionLandscape();
        setDataToSpinnerCities();
        setOnBtnOkEnterCityClickListener();
        setOnBtnOkSpinnerCityClickListener();
    }

    private void checkIsPositionLandscape(){
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void initViews(View view) {
        chooseCity = view.findViewById(R.id.chooseYourCity);
        citiesSpinner = view.findViewById(R.id.cities);
        enterCity = view.findViewById(R.id.enterCity);
        okEnterCity = view.findViewById(R.id.okEnterCity);
        okSpinnerCity = view.findViewById(R.id.okSpinnerCity);
    }

    private void setDataToSpinnerCities() {
        chooseCityActivityPresenter.takeCitiesListFromResources(getResources());
        chooseCityActivityPresenter.getDataFromCitiesSpinner(Objects.requireNonNull(getActivity()), citiesSpinner);
    }

    private void setOnBtnOkEnterCityClickListener() {
        View.OnClickListener btnOkClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Toast.makeText(Objects.requireNonNull(getActivity()), enterCity.getText(), Toast.LENGTH_SHORT).show();
            if (!enterCity.getText().toString().equals("")) {
                currentCity = enterCity.getText().toString();
                Toast.makeText(Objects.requireNonNull(getActivity()), currentCity, Toast.LENGTH_SHORT).show();
                //Обновляем данные погоды, если положение горизонтальное или открываем новое активити, если вертикальное
                updateWeatherData();
                // Добавляем новый город в спиннер
                chooseCityActivityPresenter.addCityToCitiesSpinner(currentCity, Objects.requireNonNull(getActivity()));
            }
            }
        };
        okEnterCity.setOnClickListener(btnOkClickListener);
    }

    private void setOnBtnOkSpinnerCityClickListener() {
        okSpinnerCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            currentCity = citiesSpinner.getSelectedItem().toString();

            Toast.makeText(Objects.requireNonNull(getActivity()), currentCity, Toast.LENGTH_SHORT).show();
            // Ставим выбранный город на первое место в коллекции, а следовательно в спиннере:
            chooseCityActivityPresenter.putChosenCityToTopInCitiesSpinner(currentCity);
            //Обновляем данные погоды, если положение горизонтальное или открываем новое активити, если вертикальное
            updateWeatherData();
            }
        });
    }

    private void updateWeatherData(){
        if(isLandscape) {
            chooseCityActivityPresenter.updateWeatherInLandscape(getCurrentCityContainer(), Objects.requireNonNull(getFragmentManager()));
        } else {
            Intent intent = new Intent();
            intent.setClass(Objects.requireNonNull(getActivity()), MainActivity.class);
            // и передадим туда параметры
            intent.putExtra("currCity", getCurrentCityContainer());
            startActivity(intent);
            getActivity().finish();
        }
    }

    public CurrentCityContainer getCurrentCityContainer() {
        CurrentCityContainer container = new CurrentCityContainer();
        container.currCityName = currentCity;
        if (!isLandscape) {
            CurrentCityContainer ccc = (CurrentCityContainer) getActivity().getIntent().getSerializableExtra("currCity");
            boolean[] switchSettingsArray = ccc.switchSettingsArray;
            if (switchSettingsArray != null) {
                container.switchSettingsArray = switchSettingsArray;
                Log.d(myLog, "CHOOSE CITY FRAGMENT: getCurrentCityContainer(); !isLandscape; switchSettingsArray != null");
            }
        } else {
            CurrentCityContainer currentCityContainer = (CurrentCityContainer) getArguments().getSerializable("currCity");
            if (currentCityContainer != null) container.switchSettingsArray = currentCityContainer.switchSettingsArray;
            Log.d(myLog, "CHOOSE CITY FRAGMENT: getCurrentCityContainer(); else; currentCityContainer != null");
        }
        return container;
    }
}