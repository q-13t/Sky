package com.learning.sky;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long start = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, WeatherFragment.newInstance()).commit();
        findViewById(R.id.weather).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);

        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + "RunTime: " + (System.currentTimeMillis() - start));
    }

    @Override
    protected void onResume() {
        long start = System.currentTimeMillis();
        super.onResume();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        Objects.requireNonNull(getSupportActionBar()).hide();
        SwipeRefreshLayout srl = findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(
//                () -> THREAD_POOL.submit(() -> {UpdateHourlyForecast();UpdateDailyForecast();srl.setRefreshing(false);})
                () -> {
                    UpdateHourlyForecast();
                    UpdateDailyForecast();
                    srl.setRefreshing(false);
                }
        );
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + "RunTime: " + (System.currentTimeMillis() - start));
    }

    @Override
    protected void onStart() {
        long start = System.currentTimeMillis();
        super.onStart();
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + "RunTime: " + (System.currentTimeMillis() - start));
    }

    @SuppressLint("SetTextI18n")
    private void UpdateDailyForecast() {

        long start = System.currentTimeMillis();

        LinearLayout parent = findViewById(R.id.dailyForecast);
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

        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + "RunTime: " + (System.currentTimeMillis() - start));

    }

    @SuppressLint("SetTextI18n")
    public void UpdateHourlyForecast() {
        long start = System.currentTimeMillis();

        LinearLayout parent = findViewById(R.id.hourlyForecast);
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

        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + "RunTime: " + (System.currentTimeMillis() - start));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.settings: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
                break;
            }
            case R.id.weather: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, WeatherFragment.newInstance()).commit();
                break;
            }
        }
        ((DrawerLayout)  findViewById(R.id.main_drawer)).close();
    }
}