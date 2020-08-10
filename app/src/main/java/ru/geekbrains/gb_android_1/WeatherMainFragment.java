package ru.geekbrains.gb_android_1;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class WeatherMainFragment extends Fragment {
    private boolean isLandscape;  // Можно ли расположить рядом фрагмент с выбором города
    public static String currentCity = "City";
    private ImageButton settingsButton, locationButton, readMoreButton;
    private TextView cityTextView;
    private TextView degrees;
    private TextView windInfoTextView, pressureInfoTextView;
    final String myLog = "myLog";

    static WeatherMainFragment create(CurrentCityContainer container) {
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
            constraintSet.applyTo(constraintLayout);
            //TODO убрать значок локации из горизонтального лэйаута
        }
        setOnLocationBtnOnClick();
        setOnSettingsBtnOnClick();
        setOnReadMoreBtnOnClick();
        showCheckedSwitches();
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
        TextView center = view.findViewById(R.id.center);
        settingsButton = view.findViewById(R.id.settingsBottom);
        locationButton = view.findViewById(R.id.locationButton);
        cityTextView = view.findViewById(R.id.city);
        degrees = view.findViewById(R.id.degrees);
        windInfoTextView = view.findViewById(R.id.windInfoTextView);
        pressureInfoTextView = view.findViewById(R.id.pressureInfoTextView);
        readMoreButton = view.findViewById(R.id.readMoreButton);
    }

    private void setOnLocationBtnOnClick(){
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseCityFragment();
                getActivity().finish();//
            }
        });
    }

    private void setOnSettingsBtnOnClick() {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Objects.requireNonNull(getActivity()), SettingsActivity.class);
                startActivity(intent);
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

    public CurrentCityContainer getCurrentCityContainer() {
        CurrentCityContainer container = new CurrentCityContainer();
        container.currCityName = currentCity;
        return container;
    }

    String getCityName() {
        if(getArguments() == null){
            Log.d(myLog, "getCityName: UPDATE CHOSEN CITY: getArguments() == null");
            if (getActivity().getIntent().getSerializableExtra("currCity") != null) {
                CurrentCityContainer currentCityContainer = (CurrentCityContainer) getActivity().getIntent().getSerializableExtra("currCity");
                currentCity = currentCityContainer.currCityName;
                // Передача параметра
                Bundle args = new Bundle();
                args.putSerializable("currCity", getActivity().getIntent().getSerializableExtra("currCity"));
                this.setArguments(args);
            }
        } else {
            Log.d(myLog, "getCityName: UPDATE CHOSEN CITY: else");
            CurrentCityContainer currentCityContainer = (CurrentCityContainer) getArguments().getSerializable("currCity");
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
            if(settingsSwitchArray != null) {
                if (settingsSwitchArray[0]) Toast.makeText(getActivity(), "NightMode is on", Toast.LENGTH_SHORT).show();
                if (settingsSwitchArray[1]) windInfoTextView.setVisibility(View.VISIBLE);
                if (settingsSwitchArray[2]) pressureInfoTextView.setVisibility(View.VISIBLE);
            }
        }
}