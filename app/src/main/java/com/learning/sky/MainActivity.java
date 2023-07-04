package com.learning.sky;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import com.learning.sky.FragmentController.CitySearchFragment;
import com.learning.sky.FragmentController.SettingsFragment;
import com.learning.sky.FragmentController.WeatherFragment;
import com.learning.sky.dao.ApplicationSettings;
import com.learning.sky.dao.FileOperator;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


	public static Context main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		repaint(this);
		super.onCreate(savedInstanceState);
//		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		if (main == null) main = this;
		if (FileOperator.listFiles().length == 0) {
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CitySearchFragment.newInstance()).commit();
		} else {
//			TODO: set default city weather from Files
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, WeatherFragment.newInstance()).commit();
			WeatherFragment.FillDataFromFile((String) ApplicationSettings.getPreferenceValue(PreferenceType.STRING,"DEFAULT_LOCATION",this));
		}

//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View view) {
		boolean flag = true;
		switch (view.getId()) {
			case (R.id.settings): {
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
				break;
			}
			case (R.id.weather): {
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, WeatherFragment.newInstance()).commit();
				break;
			}
			case (R.id.city_search): {
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CitySearchFragment.newInstance()).commit();
				break;
			}
			case (R.id.burger_side_menu): {
				((DrawerLayout) findViewById(R.id.main_drawer)).open();
				flag = false;
				break;
			}
		}
		if (flag)
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}