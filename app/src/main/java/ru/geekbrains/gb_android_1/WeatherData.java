package ru.geekbrains.gb_android_1;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

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

    public WeatherData(Resources resources, String degrees, String windInfo, String pressure, String weatherStateInfo, String feelLike, String weatherIcon){
        String tempSign;
        float t = Float.parseFloat(degrees.trim());
        Log.d("myLog", "Degrees float from internet = " + t);
        if(t > 0) {tempSign = "+";} else {tempSign = "";}
        String stringTemperature = String.valueOf(Math.round(t));
        this.degrees = tempSign + stringTemperature +  "°";

        String windInfoFromRes = resources.getString(R.string.windInfo);
        this.windInfo = String.format(windInfoFromRes, windInfo);

        String pressureInfoFromRes = resources.getString(R.string.pressureInfo);
        this.pressure = String.format(pressureInfoFromRes, pressure);

        this.weatherStateInfo = weatherStateInfo;

        String feelsLikeInfoFromRes = resources.getString(R.string.feels_like_temp);
        String sign;
        float f = Float.parseFloat(feelLike.trim());
        Log.d("myLog", "FeelsLike float from internet = " + f);
        if(f > 0) {sign = "+";} else {sign = "";}
        String stringFeelLike = String.valueOf(Math.round(f));
        this.feelLike = String.format(feelsLikeInfoFromRes, sign, stringFeelLike);

        this.weatherIcon = weatherIcon;
    }

    // Контруктор для создания рандомных данных погоды:
    public WeatherData(Resources resources){
        calculateRandomValues();
        degrees = "+" + tempRandom + "°";

        String feelsLikeInfoFromRes = resources.getString(R.string.feels_like_temp);
        feelLike = String.format(feelsLikeInfoFromRes, "+", (String.valueOf(tempRandom - 2)));

        String pressureInfoFromRes = resources.getString(R.string.pressureInfo);
        pressure = String.format(pressureInfoFromRes, String.valueOf(pressureRandom));

        String windInfoFromRes = resources.getString(R.string.windInfo);
        windInfo = String.format(windInfoFromRes, String.valueOf(windRandom));

        String[] weatherStateInfoFromRes = resources.getStringArray(R.array.weatherState);
        int weatherStateIndex = (int)(Math.random() * weatherStateInfoFromRes.length);
        weatherStateInfo = weatherStateInfoFromRes[weatherStateIndex];

        String[] weatherIconsFromRes = resources.getStringArray(R.array.iconsId);
        weatherIcon = weatherIconsFromRes[weatherStateIndex];
    }

    @NonNull
    @Override
    public String toString() {
        String weatherData = " - WEATHER DATA: degrees = " + degrees +
                " windInfo = " + windInfo +
                " pressure = " + pressure +
                " weatherStateInfo = " + weatherStateInfo +
                " feelLike = " + feelLike +
                " weatherIcon = " + weatherIcon;
        return weatherData;
    }

    private void calculateRandomValues(){
        tempRandom = (int)(8 + Math.random() * 10);
        windRandom = (int)(Math.random() * 10);
        pressureRandom = 700 + (int)(Math.random() * 50);
    }
}
