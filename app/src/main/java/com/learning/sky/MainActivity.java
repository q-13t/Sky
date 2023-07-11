package com.learning.sky;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.learning.sky.FragmentController.CitySearchFragment;
import com.learning.sky.FragmentController.SettingsFragment;
import com.learning.sky.FragmentController.WeatherFragment;
import com.learning.sky.dao.ApplicationSettings;
import com.learning.sky.dao.FileOperator;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	public static WeakReference<Context> main;
	private static Executor executor;
	private static Handler handler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		repaint(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@SuppressLint("SetTextI18n")
	@Override
	protected void onStart() {
		super.onStart();
		main = new WeakReference<>(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		File[] listFiles = FileOperator.listFiles();


		if (listFiles.length == 0) {
			changeFragment(CitySearchFragment.newInstance());
		} else {
			WeatherFragment weatherFragment = WeatherFragment.newInstance();
			changeFragment(weatherFragment);

			if (executor == null) {
				executor = Executors.newSingleThreadExecutor();
				handler = new Handler(Looper.getMainLooper());
			}
			executor.execute(() -> {//Read All Files Containing Forecast Data
				ArrayList<JsonObject> list = new ArrayList<>();
				for (File f : listFiles) {
					list.add(FileOperator.readFile(f.getName()));
				}
				handler.post(() -> {//Populate Weather Page
					weatherFragment.PopulateForecast(list.get(0));
				});
				handler.post(() -> {//Populate NavMenu
					PopulateSideMenu(list);
				});
			});

		}
	}

	@SuppressLint("SetTextI18n")
	public void PopulateSideMenu(ArrayList<JsonObject> list) {
		LinearLayout sideNav = findViewById(R.id.brief_data_container);
		sideNav.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			getLayoutInflater().inflate(R.layout.brief_detail, sideNav, true);

			LinearLayout childAt = (LinearLayout) sideNav.getChildAt(i);
			childAt.setOnClickListener((View view) -> {
				executor.execute(() -> {
					WeatherFragment weatherFragment = WeatherFragment.newInstance();
					JsonObject data = FileOperator.readFile((String) ((TextView) ((LinearLayout) view).getChildAt(0)).getText());
					changeFragment(weatherFragment);
					handler.post(() -> {
						weatherFragment.PopulateForecast(data);
						((DrawerLayout) findViewById(R.id.main_drawer)).close();
					});
				});
			});
			JsonObject object = list.get(i);
			((TextView) childAt.getChildAt(0)).setText(FileOperator.getCityName(object));
			((ImageView) childAt.getChildAt(1)).setImageDrawable(AppCompatResources.getDrawable(this, WeatherFragment.getIconName(object.get("list").getAsJsonArray().get(0).getAsJsonObject())));
			((TextView) childAt.getChildAt(2)).setText(WeatherFragment.getAsJsonArray(object).get(0).getAsJsonObject().get("main").getAsJsonObject().get("feels_like").getAsInt() + getString(R.string.degree_sign));
		}
	}

	public void updateSideNavChild(JsonObject element) {

	}

	public void changeFragment(Fragment to) {
		try {
			Fragment possessed = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
			if (possessed != to)
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, to).commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

	@Override
	public void onClick(View view) {
		boolean flag = true;
		switch (view.getId()) {
			case (R.id.settings): {
				changeFragment(SettingsFragment.newInstance());
				break;
			}
			case (R.id.weather): {
				changeFragment(WeatherFragment.newInstance());
				break;
			}
			case (R.id.city_search): {
				changeFragment(CitySearchFragment.newInstance());
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