package com.learning.sky;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MainActivity extends AppCompatActivity {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long start = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + "RunTime: " + (System.currentTimeMillis() - start));
    }

    @Override
    protected void onResume() {
        long start = System.currentTimeMillis();
        super.onResume();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        SwipeRefreshLayout srl = findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(
                () -> THREAD_POOL.submit(() -> {
                    UpdateHourlyForecast();
                    UpdateDailyForecast();
                    srl.setRefreshing(false);
                })
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
            TextView day = (TextView) child.getChildAt(0);
            TextView rain_percentage = (TextView)child.getChildAt(4);
            rain_percentage.setText(random.nextInt(101) + "%");
//            TODO : temperature is not setting, blocks loop
            TextView temperature = (TextView) child.getChildAt(5);
            temperature.setText("15");
        }
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + "RunTime: " + (System.currentTimeMillis() - start));
    }

    @SuppressLint("SetTextI18n")
    public void UpdateHourlyForecast(){
        long start = System.currentTimeMillis();

        LinearLayout parent = findViewById(R.id.hourlyForecast);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ConstraintLayout child = (ConstraintLayout) parent.getChildAt(i);
            ImageView weather_hourly_ico = (ImageView) child.getChildAt(0);
            TextView rain_percentage = (TextView)child.getChildAt(1);
            rain_percentage.setText(random.nextInt(101) + "%");
            TextView temperature = (TextView)child.getChildAt(2);
            temperature.setText(random.nextInt(30) + "°");
            TextView time = (TextView)child.getChildAt(3);
            ImageView rain_ico =(ImageView) child.getChildAt(4);
        }

        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + "RunTime: " + (System.currentTimeMillis() - start));
    }
}