package ru.geekbrains.gb_android_1;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WeatherMainFragment extends Fragment implements RVOnItemClick {
    private boolean isLandscape;  // Можно ли расположить рядом фрагмент с выбором города
    public static String currentCity = "City";
    private TextView center;
    private ImageButton settingsButton, locationButton, readMoreButton;
    private TextView cityTextView;
    private TextView degrees;
    private TextView feelsLikeTextView, pressureInfoTextView;
    final String myLog = "myLog";
    private WeekWeatherRecyclerDataAdapter weekWeatherAdapter;
    private RecyclerView weatherRecyclerView;
    private List<Integer> weatherIcon = new ArrayList<>();
    private List<String> weatherStateInfo = new ArrayList<>();
    private List<String> days = new ArrayList<>();
    private List<String> daysTemp = new ArrayList<>();
    private TextView windInfoTextView;
    private TextView currTime;
    private TextView weatherStatusTextView;

    List<WeatherData> weekWeatherData;
    List<String> citiesList;

    static WeatherMainFragment create(CurrentDataContainer container) {
        WeatherMainFragment fragment = new WeatherMainFragment();    // создание
        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable("currCity", container);
        fragment.setArguments(args);
        return fragment;
    }

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_weather_main, container, false);
        Toast.makeText(getContext(), "onCreateView", Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        // Проверяем ориентацию экрана и в случае альбомной, меняем расположение элементов, сдвигая ниже:
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            ConstraintLayout constraintLayout = view.findViewById(R.id.full_screen_constraintlayout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.setVerticalBias(R.id.center, 0.8f);
            constraintSet.connect(R.id.degrees,ConstraintSet.BOTTOM,R.id.center,ConstraintSet.TOP,0);
            constraintSet.setVisibility(R.id.weekWeatherRV, View.GONE);
            constraintSet.setVisibility(R.id.locationButton, View.INVISIBLE);
            constraintSet.setHorizontalBias(R.id.readMoreButton, 0.1f);
            constraintSet.applyTo(constraintLayout);
        }

        setOnLocationBtnOnClick();
        setOnSettingsBtnOnClick();
        setOnReadMoreBtnOnClick();
        showCheckedSwitches();
        takeDaysListFromResources(getResources());
        addDataToWeatherIconsIdFromRes(getResources());
        addDefaultDataToDaysTempFromRes(getResources());
        updateWeatherInfo(getResources());
        setupRecyclerView();
    }

    // activity создана, можно к ней обращаться. Выполним начальные действия
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет расположить рядом выбор города в другом фрагменте
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        // Если можно нарисовать рядом выбор города, то сделаем это
        if (isLandscape) {
            showChooseCityFragment();
        }
        updateChosenCity();
        Toast.makeText(getActivity(), "Update city to " + currentCity, Toast.LENGTH_LONG).show();
        Log.d(myLog, "WeatherMainActivity: onActivityCreated; currentCity: " + currentCity);
    }

    private void initViews(View view) {
        center = view.findViewById(R.id.center);
        settingsButton = view.findViewById(R.id.settingsBottom);
        locationButton = view.findViewById(R.id.locationButton);
        cityTextView = view.findViewById(R.id.city);
        degrees = view.findViewById(R.id.degrees);
        feelsLikeTextView = view.findViewById(R.id.feelsLikeTextView);
        pressureInfoTextView = view.findViewById(R.id.pressureInfoTextView);
        readMoreButton = view.findViewById(R.id.readMoreButton);
        weatherRecyclerView = view.findViewById(R.id.weekWeatherRV);
        windInfoTextView = view.findViewById(R.id.windSpeed);
        currTime = view.findViewById(R.id.currTime);
        weatherStatusTextView = view.findViewById(R.id.cloudyInfoTextView);
    }

    private void setOnLocationBtnOnClick(){
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseCityFragment();
                getActivity().finish();
            }
        });
    }

    private void setOnSettingsBtnOnClick() {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Objects.requireNonNull(getActivity()), SettingsActivity.class);
                intent.putExtra("currCity", getCurrentCityContainer());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void setOnReadMoreBtnOnClick() {
        readMoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String wiki = "https://ru.wikipedia.org/wiki/" + currentCity;
                Uri uri = Uri.parse(wiki);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }


    // Показать выбор города. Ecли возможно, то показать рядом с погодой,
    // если нет, то открыть вторую activity
    private void showChooseCityFragment() {
        if (isLandscape) {
            // Проверим, что фрагмент с выбором города существует в activity
            ChooseCityFragment chooseCityFragment = (ChooseCityFragment)
                    Objects.requireNonNull(getFragmentManager()).findFragmentById(R.id.chooseCity);

            // Если есть необходимость, то выведем выбор города
            if (chooseCityFragment == null || chooseCityFragment.getCurrentCity() != currentCity) {

                // Создаем новый фрагмент с текущей позицией для города
                chooseCityFragment = ChooseCityFragment.create(getCurrentCityContainer());

                // Выполняем транзакцию по замене фрагмента
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.chooseCity, chooseCityFragment);  // замена фрагмента

                //Здесь можно установить анимацию для смены фрагмента:
                //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //ft.addToBackStack(null);
//                ft.addToBackStack("Some_Key");
                ft.commit();
            }
        } else {
            // Если нельзя вывести выбор города рядом, откроем вторую activity
            Intent intent = new Intent();
            intent.setClass(Objects.requireNonNull(getActivity()), ChooseCityActivity.class);
            // и передадим туда параметры
            intent.putExtra("currCity", getCurrentCityContainer());
            startActivity(intent);
        }
    }

    public CurrentDataContainer getCurrentCityContainer() {
        CurrentDataContainer container = new CurrentDataContainer();
        container.currCityName = currentCity;
        if (!isLandscape) {
            CurrentDataContainer cdc = (CurrentDataContainer) getActivity().getIntent().getSerializableExtra("currCity");
            if(cdc != null) {
                boolean[] switchSettingsArray = cdc.switchSettingsArray;
                if (switchSettingsArray != null) {
                    container.switchSettingsArray = switchSettingsArray;
                    Log.d(myLog, "CHOOSE CITY FRAGMENT: getCurrentDataContainer(); !isLandscape; switchSettingsArray != null");
                }
                List<WeatherData> weekWeatherData = cdc.weekWeatherData;
                if (weekWeatherData.size() != 0) container.weekWeatherData = weekWeatherData;
                List<String> citiesList = cdc.citiesList;
                if (citiesList.size() != 0) container.citiesList = citiesList;
            }
        } else {
            if (getArguments() != null && getArguments().getSerializable("currCity") != null) {
                CurrentDataContainer currentCityContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
                if (currentCityContainer != null) {
                    container.switchSettingsArray = currentCityContainer.switchSettingsArray;
                    container.citiesList = currentCityContainer.citiesList;
                    container.weekWeatherData = currentCityContainer.weekWeatherData;
                }
                Log.d(myLog, "CHOOSE CITY FRAGMENT: getCurrentDataContainer(); else; currentCityContainer != null");
            }
        }
        return container;
    }

    String getCityName() {
        if(getArguments() == null){
            Log.d(myLog, "getCityName: UPDATE CHOSEN CITY: getArguments() == null");
            if (getActivity().getIntent().getSerializableExtra("currCity") != null) {
                CurrentDataContainer currentCityContainer = (CurrentDataContainer) getActivity().getIntent().getSerializableExtra("currCity");
                currentCity = currentCityContainer.currCityName;
                // Передача параметра
                Bundle args = new Bundle();
                args.putSerializable("currCity", getActivity().getIntent().getSerializableExtra("currCity"));
                this.setArguments(args);
            }
        } else {
            Log.d(myLog, "getCityName: UPDATE CHOSEN CITY: else");
            CurrentDataContainer currentCityContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
            if (currentCityContainer == null) return currentCity;
            currentCity = currentCityContainer.currCityName;
        }
        Log.d(myLog, currentCity);
        return currentCity;
    }

    private void updateChosenCity() {
        cityTextView.setText(getCityName());
        Toast.makeText(getContext(), "updateChosenCity", Toast.LENGTH_SHORT).show();
    }

    private void showCheckedSwitches(){
            boolean[] settingsSwitchArray = getActivity().getIntent().getBooleanArrayExtra(SettingsActivity.settingsDataKey);
             isSettingsSwitchArrayTransferred(settingsSwitchArray);
        }

    private  void updateWeatherInfo(Resources resources){
//        if(getArguments() == null && getActivity().getIntent() == null){
            Log.d(myLog, "updateWeatherInfo FIRST TIME" );
            degrees.setText("+0°");

            String windInfoFromRes = resources.getString(R.string.windInfo);
            windInfoTextView.setText (String.format(windInfoFromRes, 0));

            Date currentDate = new Date();
            DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeText = timeFormat.format(currentDate);
            currTime.setText(timeText);

//        }
        if (getArguments() != null) {
            CurrentDataContainer currentDataContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
            if (currentDataContainer != null) {
                Log.d(myLog, "updateWeatherInfo from Arguments" );
                boolean[] settingsSwitchArray = currentDataContainer.switchSettingsArray;
                weekWeatherData = currentDataContainer.weekWeatherData;
                citiesList = currentDataContainer.citiesList;
                isSettingsSwitchArrayTransferred(settingsSwitchArray);
                setNewWeatherData(weekWeatherData);
            }
        }
        if(getActivity().getIntent() != null) {
            CurrentDataContainer cdc = (CurrentDataContainer) getActivity().getIntent().getSerializableExtra("currCity");
            if (cdc != null) {
                boolean[] settingsSwitchArray = cdc.switchSettingsArray;
                weekWeatherData = cdc.weekWeatherData;
                citiesList = cdc.citiesList;
                isSettingsSwitchArrayTransferred(settingsSwitchArray);
                setNewWeatherData(weekWeatherData);

            }
        }
    }

    private void isSettingsSwitchArrayTransferred(boolean[] settingsSwitchArray){
        if(settingsSwitchArray != null) {
            if (settingsSwitchArray[0]) Toast.makeText(getActivity(), "NightMode is on", Toast.LENGTH_SHORT).show();
            if (settingsSwitchArray[1]) feelsLikeTextView.setVisibility(View.VISIBLE);
            if (settingsSwitchArray[2]) pressureInfoTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setNewWeatherData(List<WeatherData> weekWeatherData) {
        if (weekWeatherData.size() != 0) {
            Log.d(myLog, "УРА" + weekWeatherData.get(0).feelLike);
            WeatherData wd = weekWeatherData.get(0);
            degrees.setText(wd.degrees);
            windInfoTextView.setText(wd.windInfo);


            Date currentDate = new Date();
            DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeText = timeFormat.format(currentDate);
            currTime.setText(timeText);

            weatherStatusTextView.setText(wd.weatherStateInfo);
            pressureInfoTextView.setText(wd.pressure);
            feelsLikeTextView.setText(wd.feelLike);

            for (int i = 0; i < 7; i++) {
               WeatherData weatherData = weekWeatherData.get(i);
               daysTemp.set(i, weatherData.degrees);
               weatherStateInfo.add(i, weatherData.weatherStateInfo);
                String imageName =weatherData.weatherIcon;
                Log.d(myLog, "ICON " + i + " " +  imageName);
                Integer resID = getResources().getIdentifier(imageName , "drawable", getActivity().getPackageName());
                weatherIcon.set(i, resID);
            }
        }
    }

    public void takeDaysListFromResources(android.content.res.Resources resources){
            String[] daysStringArr = resources.getStringArray(R.array.days);
            days = Arrays.asList(daysStringArr);
    }

    public void addDefaultDataToDaysTempFromRes(android.content.res.Resources resources){
        String[] daysTempStringArr = resources.getStringArray(R.array.daysTemp);
        daysTemp  = Arrays.asList(daysTempStringArr);
    }

    public void addDataToWeatherIconsIdFromRes(android.content.res.Resources resources){
        weatherIcon.add(R.drawable.cloudy_icon);
        weatherIcon.add(R.drawable.little_cloudy_sunny);
        weatherIcon.add(R.drawable.sunny_not_cloudy);
        weatherIcon.add(R.drawable.little_cloudy_sunny);
        weatherIcon.add(R.drawable.cloudy_rainy_not_windy);
        weatherIcon.add(R.drawable.cloudy_very_rainy_and_windy);
        weatherIcon.add(R.drawable.storm_cloudy);
    }
    @Override
    public void onItemClicked(String itemText) {
        Toast.makeText(getActivity().getBaseContext(), itemText, Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        weekWeatherAdapter = new WeekWeatherRecyclerDataAdapter ( days,daysTemp,weatherIcon,this);

        weatherRecyclerView.setLayoutManager(layoutManager);
        weatherRecyclerView.setAdapter(weekWeatherAdapter);
    }
}