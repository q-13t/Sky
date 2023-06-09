package com.learning.sky;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeRefreshLayout srl = findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Random random = new Random();
                        LinearLayout parent = (LinearLayout) findViewById(R.id.hourlyForecast);
                        if(parent.getChildCount()!=0){
                            parent.removeAllViews();
                        }
                        for (int i = 1; i <= 24; i++) {
                            View custom = getLayoutInflater().inflate(R.layout.forecasth_hourly, null);
                            TextView tv1 = (TextView) custom.findViewById(R.id.rain_percentage);
                            tv1.setText(random.nextInt(1000)+"");
                            TextView tv2 = (TextView) custom.findViewById(R.id.time);
                            tv2.setText("00:00");
                            TextView tv3 = (TextView) custom.findViewById(R.id.temperature);
                            tv3.setText(random.nextInt(1000)+"*");
                            parent.addView(custom);
                        }
                        srl.setRefreshing(false);
                    }
                }
        );
    }
    public void ButtonTest(View view){
        LinearLayout parent = (LinearLayout) findViewById(R.id.hourlyForecast);
        if(parent.getChildCount()!=0){
            parent.removeAllViews();
        }


        for (int i = 1; i <= 24; i++) {
            View custom = getLayoutInflater().inflate(R.layout.forecasth_hourly, null);
            TextView tv1 = (TextView) custom.findViewById(R.id.rain_percentage);
            tv1.setText(i+"");
            TextView tv2 = (TextView) custom.findViewById(R.id.time);
            tv2.setText("00:00");
            TextView tv3 = (TextView) custom.findViewById(R.id.temperature);
            tv3.setText(i+"*");
            parent.addView(custom);
        }
    }
}