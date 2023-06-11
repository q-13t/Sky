package com.learning.sky;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long start = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        System.out.println("Create RunTime: " + (System.currentTimeMillis() - start));
    }

    @Override
    protected void onResume() {
        long start = System.currentTimeMillis();
        super.onResume();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        SwipeRefreshLayout srl = findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(
                () -> {
                    UpdateHourlyForecast(null);
                    srl.setRefreshing(false);
                }
        );
        System.out.println("Resume RunTime: " + (System.currentTimeMillis() - start));
    }

    @Override
    protected void onStart() {
        long start = System.currentTimeMillis();
        super.onStart();
//        UpdateHourlyForecast(null);
        System.out.println("Start RunTime: " + (System.currentTimeMillis() - start));
    }

    public void UpdateHourlyForecast(View view){
        long start = System.currentTimeMillis();
        Random random = new Random();
        LinearLayout parent = findViewById(R.id.hourlyForecast);
        if(parent.getChildCount()!=0){
            parent.removeAllViews();
        }
        String text;
        for (int i = 1; i <= 24; i++) {
            View custom = getLayoutInflater().inflate(R.layout.forecasth_hourly, (ViewGroup) view, false);
            TextView tv1 = custom.findViewById(R.id.rain_percentage);
            tv1.setText(String.valueOf(random.nextInt(1000)));
            TextView tv2 = custom.findViewById(R.id.time);
            text = "00:00";
            tv2.setText(text);
            TextView tv3 = custom.findViewById(R.id.temperature);
            tv3.setText(String.valueOf(random.nextInt(1000)));
            parent.addView(custom);
        }
        System.out.println("UpdateHourlyForecast RunTime: " + (System.currentTimeMillis() - start));
    }
}