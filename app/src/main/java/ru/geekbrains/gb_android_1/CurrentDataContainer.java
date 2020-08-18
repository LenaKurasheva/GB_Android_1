package ru.geekbrains.gb_android_1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CurrentDataContainer implements Serializable {
    public String currCityName = "";
    boolean[] switchSettingsArray;
    List<WeatherData> weekWeatherData = new ArrayList<>();
    ArrayList<String> citiesList = new ArrayList<>();
    static boolean isNightModeOn;
    static boolean NightIsAlreadySettedInMain;
    static boolean NightIsAlreadySettedInChoose;
}
