package ru.geekbrains.gb_android_1;

import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

public final class ChooseCityActivityPresenter {
    final String myLog = "myLog";

    //Внутреннее поле, будет хранить единственный экземпляр
    private static ChooseCityActivityPresenter instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();

//    private static String[] citiesArray;
//    private static ArrayAdapter<String> citiesListAdapter;
//    private static List<String> citiesList;

    // Конструктор (вызывать извне его нельзя, поэтому он приватный)
    private ChooseCityActivityPresenter(){}

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

    public void updateWeatherInLandscape(CurrentDataContainer container,
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


