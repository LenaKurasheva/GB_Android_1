package ru.geekbrains.gb_android_1;

import android.content.res.Resources;

import java.io.Serializable;

public class WeatherData implements Serializable {
    String degrees;
    String windInfo;
    String pressure;
    String weatherStateInfo;
    String feelLike;
    String weatherIcon;

    int tempRandom;
    int windRandom;
    int pressureRandom;

    public WeatherData(Resources resources){
        calculateRandomValues();
        degrees = "+" + tempRandom + "°";

        String feelsLikeInfoFromRes = resources.getString(R.string.feels_like_temp);
        feelLike = String.format(feelsLikeInfoFromRes, (tempRandom - 2));

        String pressureInfoFromRes = resources.getString(R.string.pressureInfo);
        pressure = String.format(pressureInfoFromRes, pressureRandom);

        String windInfoFromRes = resources.getString(R.string.windInfo);
        windInfo = String.format(windInfoFromRes, windRandom);

        String[] weatherStateInfoFromRes = resources.getStringArray(R.array.weatherState);
        int weatherStateIndex = (int)(Math.random() * weatherStateInfoFromRes.length);
        weatherStateInfo = weatherStateInfoFromRes[weatherStateIndex];

        String[] weatherIconsFromRes = resources.getStringArray(R.array.iconsId);
        weatherIcon = weatherIconsFromRes[weatherStateIndex];
    }

    private void calculateRandomValues(){
        tempRandom = (int)(8 + Math.random() * 10);
        windRandom = (int)(Math.random() * 10);
        pressureRandom = 700 + (int)(Math.random() * 50);
    }
}
