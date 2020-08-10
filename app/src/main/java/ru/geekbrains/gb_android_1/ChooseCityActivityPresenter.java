package ru.geekbrains.gb_android_1;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ChooseCityActivityPresenter {
    final String myLog = "myLog";

    //Внутреннее поле, будет хранить единственный экземпляр
    private static ChooseCityActivityPresenter instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();

    private static String[] citiesArray;
    private static ArrayAdapter<String> citiesListAdapter;
    private static List<String> citiesList;

    // Конструктор (вызывать извне его нельзя, поэтому он приватный)
    private ChooseCityActivityPresenter(){}

    public List<String> getCitiesList(){return citiesList;}

    public void takeCitiesListFromResources(android.content.res.Resources resources){
        if (citiesArray == null) {
            citiesArray = resources.getStringArray(R.array.cities);
            citiesList = new ArrayList<>(Arrays.asList(citiesArray));
        }
    }

    public void getDataFromCitiesSpinner(Context context, Spinner spinner){
        citiesListAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, citiesList);
        citiesListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(citiesListAdapter);
    }

    public void addCityToCitiesSpinner(String city, Context context){
        citiesList.add(0, city);
        citiesListAdapter.notifyDataSetChanged();
        Toast.makeText(context, "added city: " + citiesList.get(0), Toast.LENGTH_LONG).show();
    }

    public void putChosenCityToTopInCitiesSpinner(String chosenCity){
        Collections.swap(citiesList, 0 , citiesList.indexOf(chosenCity));
    }

    // Метод, который возвращает экземпляр объекта.
    // Если объекта нет, то создаем его.
    public static ChooseCityActivityPresenter getInstance(){
        // Здесь реализована «ленивая» инициализация объекта,
        // то есть, пока объект не нужен, не создаем его.
        synchronized (syncObj) {
            if (instance == null) {
                instance = new ChooseCityActivityPresenter();
            }
            return instance;
        }
    }

    public void updateWeatherInLandscape(CurrentCityContainer container,
                                         androidx.fragment.app.FragmentManager fragmentManager) {
        WeatherMainFragment weatherMainFragment = (WeatherMainFragment) fragmentManager.findFragmentById(R.id.weatherMain);
       if(weatherMainFragment == null || !weatherMainFragment.getCityName().equals(ChooseCityFragment.currentCity)) {
           Log.d(myLog, "inside updateWeatherInLandscape");
           weatherMainFragment = WeatherMainFragment.create(container);
           // Выполняем транзакцию по замене фрагмента
           FragmentTransaction ft = fragmentManager.beginTransaction();
           ft.replace(R.id.weatherMain, weatherMainFragment);  // замена фрагмента

           // можно добавить анимацию + добавить фрагмент в бэкстек:
           // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
           // ft.addToBackStack(null);
           // ft.addToBackStack("Some_Key");
           ft.commit();
       }
    }
}


