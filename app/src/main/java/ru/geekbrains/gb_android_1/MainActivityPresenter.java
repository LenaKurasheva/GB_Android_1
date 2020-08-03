package ru.geekbrains.gb_android_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Switch;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public final class MainActivityPresenter {

    //Внутреннее поле, будет хранить единственный экземпляр
    private static MainActivityPresenter instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();

    private static boolean isNightOn;

    // Конструктор (вызывать извне его нельзя, поэтому он приватный)
    private MainActivityPresenter(){
        isNightOn = false;
    }

    public boolean getIsNightOn(){return isNightOn;}

    public void setCurrentMode(ConstraintLayout constraintLayout, Switch nightMode, Context context){
        if (!isNightOn){
            int backDayColor = ContextCompat.getColor(context, R.color.day);
            constraintLayout.setBackgroundColor(backDayColor);
            int nightModeColor = ContextCompat.getColor(context, R.color.dark_grey);
            nightMode.setTextColor(nightModeColor);
        } else {
            int backNightColor = ContextCompat.getColor(context, R.color.night);
            constraintLayout.setBackgroundColor(backNightColor);
            int colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
            nightMode.setTextColor(colorAccent);
        }
    }

    public void changeNightModeStatus(){
        isNightOn = !isNightOn;
    }

    // Метод, который возвращает экземпляр объекта.
    // Если объекта нет, то создаем его.
    public static MainActivityPresenter getInstance(){
        // Здесь реализована «ленивая» инициализация объекта,
        // то есть, пока объект не нужен, не создаем его.
        synchronized (syncObj) {
            if (instance == null) {
                instance = new MainActivityPresenter();
            }
            return instance;
        }
    }
}

