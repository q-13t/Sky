package com.learning.sky;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.learning.sky.dao.ApplicationSettings;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private static Fragment lastFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		repaint(this);
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


	}

	@Override
	protected void onResume() {
		super.onResume();
		if(lastFragment!=null){
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lastFragment).commit();
		}else{
			lastFragment = WeatherFragment.newInstance();
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, WeatherFragment.newInstance()).commit();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.settings: {
				lastFragment = SettingsFragment.newInstance();
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lastFragment).commit();
				break;
			}
			case R.id.weather: {
				lastFragment = WeatherFragment.newInstance();
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lastFragment).commit();
				break;
			}
			case R.id.city_search: {
				lastFragment = CitySearchFragment.newInstance();
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lastFragment).commit();
				break;
			}
		}
		((DrawerLayout) findViewById(R.id.main_drawer)).close();
	}

	public void repaint(Context context) {
		if (Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.BOOLEAN, context.getString(R.string.CUSTOM_MODE), context), true)) {
			if (Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.BOOLEAN, context.getString(R.string.DARK_MODE), context), true)) {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			} else {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			}
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		}
	}
}