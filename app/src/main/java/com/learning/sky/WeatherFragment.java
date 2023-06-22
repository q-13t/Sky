package com.learning.sky;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Random;

public class WeatherFragment extends Fragment {
    private static final Random random = new Random();
    private View fragment;

    public WeatherFragment() {

    }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragment == null) {
            fragment = inflater.inflate(R.layout.fragment_weather, container, false);
            SwipeRefreshLayout srl = fragment.findViewById(R.id.swipeRefreshLayout);
            srl.setOnRefreshListener(
//                () -> THREAD_POOL.submit(() -> {UpdateHourlyForecast();UpdateDailyForecast();srl.setRefreshing(false);})
                    () -> {
                        UpdateHourlyForecast();
                        UpdateDailyForecast();
                        srl.setRefreshing(false);
                    });
        }


        return fragment;
    }

    @SuppressLint("SetTextI18n")
    private void UpdateDailyForecast() {


        LinearLayout parent = fragment.findViewById(R.id.dailyForecast);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            LinearLayout child = (LinearLayout) parent.getChildAt(i);
//            TextView day = (TextView) child.getChildAt(0);

//            ImageView weather_day = (ImageView) child.getChildAt(1);
//            ImageView weather_night = (ImageView) child.getChildAt(2);


            TextView rain_percentage = (TextView) child.getChildAt(4);
            rain_percentage.setText(random.nextInt(101) + "%");

            TextView temperature_day = (TextView) child.getChildAt(5);
            temperature_day.setText(random.nextInt(30) + "°");

            TextView temperature_night = (TextView) child.getChildAt(child.getChildCount() - 1);
            temperature_night.setText(random.nextInt(30) + "°");
        }


    }

    @SuppressLint("SetTextI18n")
    public void UpdateHourlyForecast() {

        LinearLayout parent = fragment.findViewById(R.id.hourlyForecast);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            LinearLayout child = (LinearLayout) parent.getChildAt(i);

//            ImageView weather_hourly_ico = (ImageView) child.getChildAt(0);

            TextView rain_percentage = (TextView) ((LinearLayout) child.getChildAt(1)).getChildAt(1);
            rain_percentage.setText(random.nextInt(101) + "%");

            TextView temperature = (TextView) child.getChildAt(2);
            temperature.setText(random.nextInt(30) + "°");


//            TextView time = (TextView)child.getChildAt(3);
        }

    }
}