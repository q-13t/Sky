package com.learning.sky;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import com.learning.sky.dao.ApplicationSettings;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        repaint(this);
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.weather).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.STRING, getString(R.string.LAST_PAGE), this), getString(R.string.DEFAULT))) {
            ApplicationSettings.setPreferenceValue(getString(R.string.LAST_PAGE), getString(R.string.WEATHER),this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, WeatherFragment.newInstance()).commit();
        } else {
            if (Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.STRING, getString(R.string.LAST_PAGE), this), R.string.WEATHER)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, WeatherFragment.newInstance()).commit();
            } else if (Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.STRING, getString(R.string.LAST_PAGE), this), getString(R.string.SETTINGS))) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
            }
        }
//        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
                ApplicationSettings.setPreferenceValue(getString(R.string.LAST_PAGE), getString(R.string.SETTINGS),this);
                break;
            }
            case R.id.weather: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, WeatherFragment.newInstance()).commit();
                ApplicationSettings.setPreferenceValue(getString(R.string.LAST_PAGE), getString(R.string.WEATHER),this);
                break;
            }
        }
        ((DrawerLayout) findViewById(R.id.main_drawer)).close();
    }

    public void repaint(Context context) {
        if(Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.BOOLEAN, context.getString(R.string.CUSTOM_MODE), context), true)){
            if(Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.BOOLEAN, context.getString(R.string.DARK_MODE), context), true)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}