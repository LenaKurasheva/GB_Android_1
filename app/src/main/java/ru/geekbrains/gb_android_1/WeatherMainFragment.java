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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
    public static String currentCity;
    private ImageButton settingsButton, locationButton, readMoreButton;
    private TextView cityTextView;
    private TextView degrees;
    private TextView feelsLikeTextView, pressureInfoTextView;
    final String myLog = "myLog";
    private RecyclerView weatherRecyclerView;
    private List<Integer> weatherIcon = new ArrayList<>();
    private List<String> weatherStateInfo = new ArrayList<>();
    private List<String> days = new ArrayList<>();
    private List<String> daysTemp = new ArrayList<>();
    private TextView windInfoTextView;
    private TextView currTime;
    private TextView weatherStatusTextView;
    private FloatingActionButton fab;
    private static boolean isNight;
    private ArrayList<String> citiesListFromRes;

    static WeatherMainFragment create(CurrentDataContainer container) {
        WeatherMainFragment fragment = new WeatherMainFragment();    // создание
        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable("currCity", container);
        fragment.setArguments(args);
        Log.d("myLog", "WeatherMainFragment CREATE");
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("myLog", "onCreate - fragment WeatherMainFragment");
        // В этот блок мы заходим, только когда пересоздаем активити
//        isLandscape = getResources().getConfiguration().orientation
//                == Configuration.ORIENTATION_LANDSCAPE;
//        if (isLandscape) {
//            Log.d(myLog, " it IsLandscape");
//            if (CurrentDataContainer.isNightModeOn) {
//                Objects.requireNonNull(getActivity()).setTheme(R.style.NoToolbarDarkTheme);
//            } else {
//                Log.d(myLog, "NoToolbarTheme");
//                Objects.requireNonNull(getActivity()).setTheme(R.style.NoToolbarTheme);
//            }
//        } else {
//            if (CurrentDataContainer.isNightModeOn) {
//                Objects.requireNonNull(getActivity()).setTheme(R.style.AppThemeDark);
//            } else {
//                Log.d(myLog, "AppTheme");
//                Objects.requireNonNull(getActivity()).setTheme(R.style.AppTheme);
//            }
//        }
        super.onCreate(savedInstanceState);
    }

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("myLog", "onCreateView - fragment WeatherMainFragment");
        View view = getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_weather_main, container, false);
        Toast.makeText(getContext(), "onCreateView", Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        // Проверяем ориентацию экрана и в случае альбомной, меняем расположение элементов:
        moveViewsIfLandscapeOrientation(view);
        setOnLocationBtnOnClick();
        setOnSettingsBtnOnClick();
        setOnReadMoreBtnOnClick();
        takeCitiesListFromResources(getResources());
        takeDaysListFromResources(getResources());
        addDataToWeatherIconsIdFromRes();
        addDefaultDataToDaysTempFromRes(getResources());
        updateWeatherInfo(getResources());
        setupRecyclerView();
        setOnFAB();
        super.onViewCreated(view, savedInstanceState);
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
            showChooseCityFragment(getCurrentCityContainer());
        }
        updateChosenCity();
        Toast.makeText(getActivity(), "Update city to " + currentCity, Toast.LENGTH_LONG).show();
        Log.d(myLog, "WeatherMainFragment: onActivityCreated; currentCity: " + currentCity);
    }

    private void moveViewsIfLandscapeOrientation( View view){
        // Проверяем ориентацию экрана и в случае альбомной, меняем расположение элементов:
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            ConstraintLayout constraintLayout = view.findViewById(R.id.full_screen_constraintlayout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.setVerticalBias(R.id.center, 0.67f);
            constraintSet.connect(R.id.degrees,ConstraintSet.BOTTOM,R.id.center,ConstraintSet.TOP,0);
            constraintSet.setVisibility(R.id.weekWeatherRV, View.GONE);
            constraintSet.setVisibility(R.id.locationButton, View.INVISIBLE);
            constraintSet.setHorizontalBias(R.id.readMoreButton, 0.1f);
            constraintSet.applyTo(constraintLayout);
        } else {fab.setVisibility(View.INVISIBLE);}
    }

    private void initViews(View view) {
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
        fab = view.findViewById(R.id.fab);
    }

    private void setOnFAB(){
        fab.setOnClickListener(view -> Snackbar.make(view, R.string.toDeleteCity, Snackbar.LENGTH_LONG)
                .setAction(R.string.delete, v -> {
                    CurrentDataContainer newCurDataCont = getCurrentCityContainer();
                    if(newCurDataCont.citiesList.size() == 0) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), R.string.pleaseChooseCotyFirst,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    newCurDataCont.citiesList.remove(0);
                    ChooseCityFragment.changeCitiesDeletedFlag();
                    showChooseCityFragment(newCurDataCont);
                    if(!isLandscape) Objects.requireNonNull(getActivity()).finish();
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), R.string.cityDeleted,
                            Toast.LENGTH_SHORT).show();
                }).show());
    }

    private void setOnLocationBtnOnClick(){
        locationButton.setOnClickListener(view -> {
            showChooseCityFragment(getCurrentCityContainer());
            Objects.requireNonNull(getActivity()).finish();
        });
    }

    private void setOnSettingsBtnOnClick() {
        settingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(Objects.requireNonNull(getActivity()), SettingsActivity.class);
            intent.putExtra("currCity", getCurrentCityContainer());
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void setOnReadMoreBtnOnClick() {
        readMoreButton.setOnClickListener(view -> {
            String wiki = "https://ru.wikipedia.org/wiki/" + currentCity;
            Uri uri = Uri.parse(wiki);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }


    // Показать выбор города. Ecли возможно, то показать рядом с погодой,
    // если нет, то открыть вторую activity
    private void showChooseCityFragment(CurrentDataContainer cdc) {
        if (isLandscape) {
            // Проверим, что фрагмент с выбором города существует в activity
            ChooseCityFragment chooseCityFragment = (ChooseCityFragment)
                    Objects.requireNonNull(getFragmentManager()).findFragmentById(R.id.chooseCity);

            // Если есть необходимость, то выведем выбор города
//            if (chooseCityFragment == null || !chooseCityFragment.getCurrentCity().equals(currentCity)) { //**************************************

                // Создаем новый фрагмент с текущей позицией для города
                chooseCityFragment = ChooseCityFragment.create(cdc);

                // Выполняем транзакцию по замене фрагмента
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.chooseCity, chooseCityFragment);  // замена фрагмента
                ft.commit();
//            } // ******************************************************************************************************
        } else {
            // Если нельзя вывести выбор города рядом, откроем вторую activity
            Intent intent = new Intent();
            intent.setClass(Objects.requireNonNull(getActivity()), ChooseCityActivity.class);
            // и передадим туда параметры
            intent.putExtra("currCity", cdc);
            startActivity(intent);
        }
    }

    public CurrentDataContainer getCurrentCityContainer() {
        CurrentDataContainer container = new CurrentDataContainer();
        container.currCityName = currentCity;
        if (!isLandscape) {
            CurrentDataContainer cdc = (CurrentDataContainer) Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra("currCity");
            if(cdc != null) {
                boolean[] switchSettingsArray = cdc.switchSettingsArray;
                if (switchSettingsArray != null) {
                    container.switchSettingsArray = switchSettingsArray;
                    Log.d(myLog, "CHOOSE CITY FRAGMENT: getCurrentDataContainer(); !isLandscape; switchSettingsArray != null");
                }
                List<WeatherData> weekWeatherData = cdc.weekWeatherData;
                if (weekWeatherData.size() != 0) container.weekWeatherData = weekWeatherData;
                ArrayList<String> citiesList = cdc.citiesList;
                if (citiesList.size() != 0) container.citiesList = citiesList;
            } else {container.citiesList = citiesListFromRes;}
            CurrentDataContainer.NightIsAlreadySettedInMain = false;
        } else {
            if (getArguments() != null && getArguments().getSerializable("currCity") != null) {
                CurrentDataContainer currentCityContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
                if (currentCityContainer != null) {
                    container.switchSettingsArray = currentCityContainer.switchSettingsArray;
                    container.citiesList = currentCityContainer.citiesList;
                    container.weekWeatherData = currentCityContainer.weekWeatherData;
                }
            } else {container.citiesList = citiesListFromRes;}
            Log.d(myLog, "CHOOSE CITY FRAGMENT: getCurrentDataContainer(); else; currentCityContainer != null");
        }
        return container;
    }

    String getCityName() {
//        if(getArguments() == null){
//            Log.d(myLog, "getCityName: UPDATE CHOSEN CITY: getArguments() == null");
        if (Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra("currCity") != null) {
            CurrentDataContainer currentCityContainer = (CurrentDataContainer) getActivity().getIntent().getSerializableExtra("currCity");
            currentCity = Objects.requireNonNull(currentCityContainer).currCityName;
            // Передача параметра
            Bundle args = new Bundle();
            args.putSerializable("currCity", getActivity().getIntent().getSerializableExtra("currCity"));
            this.setArguments(args);
            return currentCity;
        }
        if(getArguments() != null) {
            Log.d(myLog, "getCityName: UPDATE CHOSEN CITY; getArguments() != null");
            CurrentDataContainer currentCityContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
            if (currentCityContainer != null){
                currentCity = currentCityContainer.currCityName;
                return currentCity;
            }
        }
        currentCity = citiesListFromRes.get(0);
        Log.d(myLog, currentCity);
        return currentCity;
    }

    private void updateChosenCity() {
        cityTextView.setText(getCityName());
        Toast.makeText(getContext(), "updateChosenCity", Toast.LENGTH_SHORT).show();
    }

    private  void updateWeatherInfo(Resources resources){
            Log.d(myLog, "updateWeatherInfo from resources" );
            degrees.setText("+0°");

            String windInfoFromRes = resources.getString(R.string.windInfo);
            windInfoTextView.setText (String.format(windInfoFromRes, 0));

            Date currentDate = new Date();
            DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeText = timeFormat.format(currentDate);
            currTime.setText(timeText);

        List<WeatherData> weekWeatherData;
        if (getArguments() != null) {
            CurrentDataContainer currentDataContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
            if (currentDataContainer != null) {
                Log.d(myLog, "updateWeatherInfo from Arguments" );
                boolean[] settingsSwitchArray = currentDataContainer.switchSettingsArray;
                weekWeatherData = currentDataContainer.weekWeatherData;
                isSettingsSwitchArrayTransferred(settingsSwitchArray);
                setNewWeatherData(weekWeatherData);
            }
        }
        if(Objects.requireNonNull(getActivity()).getIntent() != null) {
            CurrentDataContainer cdc = (CurrentDataContainer) getActivity().getIntent().getSerializableExtra("currCity");
            if (cdc != null) {
                Log.d(myLog, "updateWeatherInfo from Intent" );
                boolean[] settingsSwitchArray = cdc.switchSettingsArray;
                weekWeatherData = cdc.weekWeatherData;
                isSettingsSwitchArrayTransferred(settingsSwitchArray);
                setNewWeatherData(weekWeatherData);

            }
        }
    }

    private void isSettingsSwitchArrayTransferred(boolean[] settingsSwitchArray){
        Log.d(myLog, "NightIsAlreadySettedInMain " + CurrentDataContainer.NightIsAlreadySettedInMain );
        Log.d(myLog, "NightMode " + CurrentDataContainer.isNightModeOn);
        if(settingsSwitchArray != null) {
//            if (settingsSwitchArray[0] && !CurrentDataContainer.NightIsAlreadySettedInMain) {
                //TODO
//                CurrentDataContainer.NightIsAlreadySettedInMain = true;
//                CurrentDataContainer.isNightModeOn = true;
//                Objects.requireNonNull(getActivity()).recreate();
//                Log.d(myLog, " RECREATE weather main fragment");
//            }
//            if (!settingsSwitchArray[0]) CurrentDataContainer.isNightModeOn = false;
            if (settingsSwitchArray[1]) feelsLikeTextView.setVisibility(View.VISIBLE);
            if (settingsSwitchArray[2]) pressureInfoTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setNewWeatherData(List<WeatherData> weekWeatherData) {
        if (weekWeatherData.size() != 0) {
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
                Integer resID = getResources().getIdentifier(imageName , "drawable", Objects.requireNonNull(getActivity()).getPackageName());
                weatherIcon.set(i, resID);
            }
        }
    }

    //переносим из  ChooseCityFragment

    public void takeCitiesListFromResources(android.content.res.Resources resources){
        String[] cities = resources.getStringArray(R.array.cities);
        List<String> cit = Arrays.asList(cities);
        citiesListFromRes = new ArrayList<>(cit);
//
//        if (this.citiesList.size() == 0) {
//            String[] cities = resources.getStringArray(R.array.cities);
//             List<String> cit = Arrays.asList(cities);
//            this.citiesList = new ArrayList<>(cit);
//        }
    }

    public void takeDaysListFromResources(android.content.res.Resources resources){
            String[] daysStringArr = resources.getStringArray(R.array.days);
            days = Arrays.asList(daysStringArr);
    }

    public void addDefaultDataToDaysTempFromRes(android.content.res.Resources resources){
        String[] daysTempStringArr = resources.getStringArray(R.array.daysTemp);
        daysTemp  = Arrays.asList(daysTempStringArr);
    }

    public void addDataToWeatherIconsIdFromRes(){
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
        Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), itemText, Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        WeekWeatherRecyclerDataAdapter weekWeatherAdapter = new WeekWeatherRecyclerDataAdapter(days, daysTemp, weatherIcon, this);

        weatherRecyclerView.setLayoutManager(layoutManager);
        weatherRecyclerView.setAdapter(weekWeatherAdapter);
    }
}