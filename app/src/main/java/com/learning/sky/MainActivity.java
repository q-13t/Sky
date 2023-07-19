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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.learning.sky.FragmentController.CityAdapter;
import com.learning.sky.FragmentController.CitySearchFragment;
import com.learning.sky.FragmentController.SettingsFragment;
import com.learning.sky.FragmentController.WeatherFragment;
import com.learning.sky.dao.ApplicationSettings;
import com.learning.sky.dao.FileOperator;
import com.learning.sky.dao.WeatherAPI;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	public static WeakReference<MainActivity> main;
	public static Executor executor;
	public static Handler handler;
	private static CityAdapter adapter;

	public static CityAdapter getAdapter() {
		return adapter;
	}


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
			executor.execute(() -> {
				adapter = new CityAdapter(this, Objects.requireNonNull(FileOperator.readCities()));
				Fragment possessed = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
				if (possessed instanceof CitySearchFragment)
					((CitySearchFragment) possessed).onAdapterReady(adapter);
			});
		}
	}

	@SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
	private void PopulateSideMenu(ArrayList<JsonObject> list) {
		LinearLayout sideNav = findViewById(R.id.brief_data_container);
		sideNav.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			getLayoutInflater().inflate(R.layout.brief_detail, sideNav, true);

			LinearLayout childAt = (LinearLayout) sideNav.getChildAt(i);
			childAt.setOnClickListener((view) ->
					executor.execute(() -> {
						WeatherFragment weatherFragment = WeatherFragment.newInstance();
						JsonObject data = FileOperator.readFile((String) ((TextView) ((LinearLayout) view).getChildAt(0)).getText());
						changeFragment(weatherFragment);
						handler.post(() -> {
							weatherFragment.PopulateForecast(data);
							((DrawerLayout) findViewById(R.id.main_drawer)).close();
						});
					})
			);
			childAt.setOnLongClickListener((view) -> {
				new AlertDialog.Builder(this)
						.setMessage("Delete " + ((TextView) ((LinearLayout) view).getChildAt(0)).getText() + "'s Data?")
						.setCancelable(false)
						.setNegativeButton("No", (dialog, which) ->
								dialog.cancel()
						)
						.setPositiveButton("Yes", (dialog, which) -> {
							String name = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();
							deleteSideNavChild(name);
							FileOperator.deleteFile(name);
							dialog.cancel();
						})
						.create().show();
				return true;
			});

			JsonObject object = list.get(i);
			((TextView) childAt.getChildAt(0)).setText(FileOperator.getCityName(object));
			((ImageView) childAt.getChildAt(1)).setImageDrawable(AppCompatResources.getDrawable(this, WeatherFragment.getIconName(object.get("list").getAsJsonArray().get(0).getAsJsonObject())));
			((TextView) childAt.getChildAt(2)).setText(WeatherFragment.getAsJsonArray(object).get(0).getAsJsonObject().get("main").getAsJsonObject().get("feels_like").getAsInt() + getString(R.string.degree_sign));
		}
	}

	@SuppressLint("SetTextI18n")
	private void updateSideNavChild(JsonObject element) {
		LinearLayout sideNav = findViewById(R.id.brief_data_container);
		boolean changed = true;
		for (int i = 0; i < sideNav.getChildCount(); i++) {
			LinearLayout childAt = (LinearLayout) sideNav.getChildAt(i);
			if (((TextView) childAt.getChildAt(0)).getText().equals(FileOperator.getCityName(element))) {
				((ImageView) childAt.getChildAt(1)).setImageDrawable(AppCompatResources.getDrawable(this, WeatherFragment.getIconName(element.get("list").getAsJsonArray().get(0).getAsJsonObject())));
				((TextView) childAt.getChildAt(2)).setText(WeatherFragment.getAsJsonArray(element).get(0).getAsJsonObject().get("main").getAsJsonObject().get("feels_like").getAsInt() + getString(R.string.degree_sign));
				changed =false;break;
			}
		}
		if(changed){
			getLayoutInflater().inflate(R.layout.brief_detail, sideNav, true);
			LinearLayout childAt = (LinearLayout) sideNav.getChildAt(sideNav.getChildCount()-1);
			((TextView) childAt.getChildAt(0)).setText(FileOperator.getCityName(element));
			((ImageView) childAt.getChildAt(1)).setImageDrawable(AppCompatResources.getDrawable(this, WeatherFragment.getIconName(element.get("list").getAsJsonArray().get(0).getAsJsonObject())));
			((TextView) childAt.getChildAt(2)).setText(WeatherFragment.getAsJsonArray(element).get(0).getAsJsonObject().get("main").getAsJsonObject().get("feels_like").getAsInt() + getString(R.string.degree_sign));
		}
	}

	private void deleteSideNavChild(String cityName) {
		LinearLayout sideNav = findViewById(R.id.brief_data_container);
		int childCount = sideNav.getChildCount();
		for (int i = 0; i < childCount; i++) {
			if (((TextView) ((LinearLayout) sideNav.getChildAt(i)).getChildAt(0)).getText().equals(cityName)) {
				sideNav.removeViewAt(i);
				break;
			}
		}
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
		adapter = null;
	}

	public void updateData(CityAdapter.City city) {
		MainActivity.executor.execute(() -> {
			JsonObject callResult = WeatherAPI.call(city);
			executor.execute(() -> {
				WeatherFragment weatherFragment = WeatherFragment.newInstance();
				changeFragment(weatherFragment);
				handler.post(()->
					weatherFragment.PopulateForecast(callResult)
				);
			});
			handler.post(() ->
				updateSideNavChild(callResult)
			);
			executor.execute(() ->
				FileOperator.writeFile(callResult)
			);
		});

	}
}