package ru.geekbrains.gb_android_1;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;


import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.gb_android_1.model.WeatherRequest;

public final class ChooseCityPresenter {
    public static final int FORECAST_DAYS = 5;
    final String myLog = "myLog";
    private static final String TAG = "WEATHER";
    String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    //Внутреннее поле, будет хранить единственный экземпляр
    private static ChooseCityPresenter instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();
    private ArrayList<WeatherData> weekWeatherData;

    // Конструктор (вызывать извне его нельзя, поэтому он приватный)
    private ChooseCityPresenter(){}

    // Метод, который возвращает экземпляр объекта.
    // Если объекта нет, то создаем его.
    public static ChooseCityPresenter getInstance(){
        // Здесь реализована «ленивая» инициализация объекта,
        // то есть, пока объект не нужен, не создаем его.
        synchronized (syncObj) {
            if (instance == null) {
                instance = new ChooseCityPresenter();
            }
            return instance;
        }
    }
    public ArrayList<WeatherData> getWeekWeatherData(){return weekWeatherData;}

    public void updateWeatherInLandscape(CurrentDataContainer container,
                                         androidx.fragment.app.FragmentManager fragmentManager) {
        WeatherMainFragment weatherMainFragment;
           Log.d(myLog, "ChooseCityFragment update updateWeatherInLandscape");
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

       public void getFiveDaysWeatherFromServer(String currentCity, Resources resources, Context context){
           try {
               final URL uri = getWeatherUrl(currentCity);
               final Handler handler = new Handler(); // Запоминаем основной поток
               new Thread(() -> {
                   HttpsURLConnection urlConnection = null;
                   try {
                       urlConnection = (HttpsURLConnection) uri.openConnection();
                       urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                       urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                       BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                       String result = getLines(in);
                       // преобразование данных запроса в модель
                       Gson gson = new Gson();
                       final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                       // Возвращаемся к основному потоку
                       handler.post(() -> {
                           getWeatherData(weatherRequest, resources);
                           Log.d(myLog, "ChooseCityPresenter - getFiveDaysWeatherFromServer - getWeatherData ");
                       });
                   } catch (Exception e) {
                       Log.e(TAG, "Fail connection", e);
                       e.printStackTrace();
                       Looper.prepare();//Call looper.prepare()
                       showToast(context, "Fail internet connection");
                       Looper.loop();
                   } finally {
                       if (null != urlConnection) {
                           urlConnection.disconnect();
                       }
                   }
               }).start();
           } catch (MalformedURLException e) {
               Log.e(TAG, "Fail URI", e);
               Looper.prepare();//Call looper.prepare()
               showToast(context, "City not exists");
               Looper.loop();
               e.printStackTrace();
           }
       }

    private URL getWeatherUrl(String cityName) throws MalformedURLException {
        return new URL(BASE_URL + "forecast?q=" + cityName + "&units=metric&appid=" + "2a72f5f940375d439b4598c5184c5e82");
    }

    private String getLines(BufferedReader reader) {
        StringBuilder rawData = new StringBuilder(1024);
        String tempVariable;

        while (true) {
            try {
                tempVariable = reader.readLine();
                if (tempVariable == null) break;
                rawData.append(tempVariable).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rawData.toString();
    }

    public void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public void getWeatherData(WeatherRequest weatherRequest, Resources resources){
        weekWeatherData = new ArrayList<>();
        for (int i = 0; i < FORECAST_DAYS; i++) {
            String degrees = String.format(Locale.getDefault(), "%s", Math.round(weatherRequest.getList().get(i).getMain().getTemp()));
            String windInfo = String.format(Locale.getDefault(), "%s", Math.round(weatherRequest.getList().get(i).getWind().getSpeed()));
            String pressure = String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(i).getMain().getPressure());
//            String weatherStateInfo = String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(i).getWeather().get(i).getDescription());
            String weatherStateInfo = "Cloudy";
            String feelLike = String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(i).getMain().getFeelsLike());
//            String weatherIcon = String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(i).getWeather().get(i).getId());
            String weatherIcon = "cloudy_icon";
            WeatherData weatherData = new WeatherData(resources, degrees, windInfo, pressure, weatherStateInfo, feelLike, weatherIcon);
            weekWeatherData.add(weatherData);
            Log.d(myLog, "WEATHER FIRS DAY " + weatherData.degrees +" " + weatherData.feelLike);
        }
    }
}



