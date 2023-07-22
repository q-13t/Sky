package com.learning.sky.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.learning.sky.R;
import com.learning.sky.model.ApplicationSettings;
import com.learning.sky.model.FileOperator;
import com.learning.sky.model.WeatherAPI;
import com.learning.sky.viewModel.CityAdapter;
import com.learning.sky.viewModel.CitySearchFragment;
import com.learning.sky.viewModel.PreferenceType;
import com.learning.sky.viewModel.SettingsFragment;
import com.learning.sky.viewModel.WeatherFragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	public static WeakReference<MainActivity> main;
	public static Executor executor;
	public static Handler handler;
	private static CityAdapter adapter;
	private Fragment savedFragment;

	public static CityAdapter getAdapter() {
		return adapter;
	}


	/**
	 * Creates new {@link #executor}, {@link #handler}, {@link WeakReference} and {@link CityAdapter} before applications activity is visible.
	 *
	 * @see CitySearchFragment#onAdapterReady
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		repaint(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (executor == null) {
			executor = Executors.newSingleThreadExecutor();
			handler = new Handler(Looper.getMainLooper());
		}
		main = new WeakReference<>(this);
		adapter = new CityAdapter(this, Objects.requireNonNull(FileOperator.readCities()));
		Fragment possessed = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (possessed instanceof CitySearchFragment)
			((CitySearchFragment) possessed).onAdapterReady(adapter);
	}

	/**
	 * Checks if application WAS restarted. If so setts current fragment to saved one.
	 * <p>
	 *
	 * <p>
	 * If application is NOT restarted
	 * -> checks for any files within {@link FileOperator#listFiles()} array
	 * -> if no were found displays CitySearch fragment.
	 * Else attempts to find "Pinned" forecast and display it and calls {@link #highlightPinnedCity}, if no was met displays first {@link JsonObject} met.
	 *
	 * @see #changeFragment
	 * @see FileOperator#listFiles()
	 * @see WeatherFragment#populateForecast
	 * @see #highlightPinnedCity
	 * @see #populateSideMenu
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (savedFragment != null) {//If -> restart set Prev Fragment
			changeFragment(savedFragment);
		} else {
			File[] listFiles = FileOperator.listFiles();
			if (listFiles.length == 0) {// If there are no files with JSON data set CitySearchFragment
				changeFragment(CitySearchFragment.newInstance());
			} else {
				WeatherFragment weatherFragment = WeatherFragment.newInstance();
				changeFragment(weatherFragment);
				ArrayList<JsonObject> list = new ArrayList<>();
				for (File f : listFiles) {// Read all present files into ArrayList
					list.add(FileOperator.readFile(f.getName()));
				}
				handler.post(() -> {//Populate NavMenu
					populateSideMenu(list);
				});
				String pinned = (String) ApplicationSettings.getPreferenceValue(PreferenceType.STRING, getString(R.string.PINNED_CITY), this);
				boolean set = false;
				if (pinned != null && !pinned.equals("DEFAULT")) {// IF there is any pinned City -> Search Files for it
					for (int i = 0; i < listFiles.length; i++) {
						if (listFiles[i].getName().equalsIgnoreCase(pinned)) {//If files name matches pinned city
							JsonObject jsonObject = list.get(i);
							handler.post(() ->
									weatherFragment.populateForecast(jsonObject)
							);
							handler.post(() ->
									highlightPinnedCity(WeatherFragment.getCityName(jsonObject))
							);
							set = true;
							break;
						}
					}
				}
				if (!set)// If File was not found set content to first File
					handler.post(() ->
							weatherFragment.populateForecast(list.get(0))
					);
			}
		}
	}


	/**
	 * Saves currently displaying fragment in case user will return to Activity.
	 */
	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		savedFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
	}

	/**
	 * Setts {@link #adapter} to null to be GarbageCollected.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter = null;
		System.gc();
	}

	/**
	 * Populates side navigation menu with brief details of forecast based on data within {@link JsonObject}s array.
	 *
	 * @param list containing {@link JsonObject}s containing forecast data.
	 * @see #insertNewBriefDetail
	 */
	@SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
	private void populateSideMenu(ArrayList<JsonObject> list) {
		LinearLayout sideNav = findViewById(R.id.brief_data_container);
		sideNav.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			insertNewBriefDetail(list.get(i), sideNav);
		}
	}

	/**
	 * Setts pinned cities brief data star to cyan color, colorOnPrimary for the rest.
	 *
	 * @param name of pinned city.
	 */
	private void highlightPinnedCity(@Nullable String name) {
		TypedArray typedArray = getTheme().obtainStyledAttributes(new int[]{com.google.android.material.R.attr.colorOnPrimary});
		int color = typedArray.getColor(0, 0);
		typedArray.recycle();
		LinearLayout sideNav = findViewById(R.id.brief_data_container);
		for (int i = 0; i < sideNav.getChildCount(); i++) {
			LinearLayout childAt = (LinearLayout) sideNav.getChildAt(i);
			if (((TextView) childAt.getChildAt(0)).getText().equals(name)) {//Check City Name displayed in brief detail with name provided
				((ImageButton) childAt.getChildAt(3)).getDrawable().setTint(getColor(R.color.cyan));
			} else {
				((ImageButton) childAt.getChildAt(3)).getDrawable().setTint(color);
			}
		}
	}

	/**
	 * Updates brief detail of updated city or adds new brief data to Side Navigation Menu.
	 *
	 * @param element containing data for update/insertion.
	 * @see #insertNewBriefDetail
	 */
	@SuppressLint("SetTextI18n")
	private void updateSideNavChild(JsonObject element) {
		LinearLayout sideNav = findViewById(R.id.brief_data_container);
		boolean changed = true;
		String cityName = WeatherFragment.getCityName(element);
		for (int i = 0; i < sideNav.getChildCount(); i++) {
			LinearLayout childAt = (LinearLayout) sideNav.getChildAt(i);
			if (((TextView) childAt.getChildAt(0)).getText().equals(cityName)) {//Check if City name in brief detail matches name to update
				((ImageView) childAt.getChildAt(1)).setImageDrawable(AppCompatResources.getDrawable(this, WeatherFragment.getIconID(element.get("list").getAsJsonArray().get(0).getAsJsonObject())));// set weather icon
				((TextView) childAt.getChildAt(2)).setText(WeatherFragment.getAsJsonArray(element).get(0).getAsJsonObject().get("main").getAsJsonObject().get("feels_like").getAsInt() + getString(R.string.degree_sign));//set feels like temperature
				changed = false;
				break;
			}
		}
		if (changed) {// If no element was present with provided city name insert new
			insertNewBriefDetail(element, sideNav);
		}
	}

	/**
	 * Inflates new View to be added to Side Navigation.
	 *
	 * @param element to be added
	 * @param sideNav to add element to
	 * @see #highlightPinnedCity
	 * @see WeatherFragment
	 * @see #changeFragment
	 * @see #deleteSideNavChild
	 * @see FileOperator
	 */
	@SuppressLint("SetTextI18n")
	private void insertNewBriefDetail(JsonObject element, LinearLayout sideNav) {
		getLayoutInflater().inflate(R.layout.brief_detail, sideNav, true);
		LinearLayout childAt = (LinearLayout) sideNav.getChildAt(sideNav.getChildCount() - 1);
		String name = WeatherFragment.getCityName(element);
		childAt.setOnClickListener((view) ->//On short click -> display forecast data
				executor.execute(() -> {
					WeatherFragment weatherFragment = WeatherFragment.newInstance();
					JsonObject data = FileOperator.readFile((String) ((TextView) ((LinearLayout) view).getChildAt(0)).getText());
					changeFragment(weatherFragment);// Change fragment to weather
					handler.post(() -> {
						weatherFragment.populateForecast(data); // fill forecast
						((DrawerLayout) findViewById(R.id.main_drawer)).close();
					});
				})
		);
		childAt.setOnLongClickListener((view) -> {//On long click display alert questioning weather to delete forecast
			new AlertDialog.Builder(this).setMessage("Delete " + name)
					.setCancelable(false)
					.setPositiveButton("Yes", (dialog, which) -> {
						deleteSideNavChild(name);
						FileOperator.deleteFile(name);
						dialog.cancel();
					})
					.setNegativeButton("No", (dialog, which) ->
							dialog.cancel()
					).create().show();

			return true;
		});

		((TextView) childAt.getChildAt(0)).setText(name);// set city name
		((ImageView) childAt.getChildAt(1)).setImageDrawable(AppCompatResources.getDrawable(this, WeatherFragment.getIconID(element.get("list").getAsJsonArray().get(0).getAsJsonObject())));//set weather icon
		((TextView) childAt.getChildAt(2)).setText(WeatherFragment.getAsJsonArray(element).get(0).getAsJsonObject().get("main").getAsJsonObject().get("feels_like").getAsInt() + getString(R.string.degree_sign));//set feels like temperature
		childAt.getChildAt(3).setOnClickListener(v -> {// set pinned city listener
			ApplicationSettings.setPreferenceValue(this.getString(R.string.PINNED_CITY), name, this);//save preference
			highlightPinnedCity(name);
		});
	}


	/**
	 * Deletes View containing reference to provided city name.
	 *
	 * @param cityName of element to be deleted.
	 */
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

	/**
	 * Fills fragment_container with one of {@link WeatherFragment}, {@link CitySearchFragment} or {@link SettingsFragment} if was not previously set to itself.
	 *
	 * @param to fragment to be set.
	 */
	public void changeFragment(Fragment to) {
		try {
			Fragment possessed = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
			if (possessed != to)// If fragment is already set -> ignore else change to new one
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, to).commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

	/**
	 * Controls  {@link SettingsFragment} and {@link CitySearchFragment} buttons in Side and Top menus.
	 * <p>
	 * Sets fragment to respective.
	 *
	 * @param view The view that was clicked.
	 * @see #changeFragment
	 */
	@Override
	public void onClick(@NonNull View view) {
		boolean flag = false;
		switch (view.getId()) {
			case (R.id.settings): {
				changeFragment(SettingsFragment.newInstance());
				break;
			}
			case (R.id.city_search): {
				changeFragment(CitySearchFragment.newInstance());
				break;
			}
			case (R.id.burger_side_menu): {
				((DrawerLayout) findViewById(R.id.main_drawer)).open();
				flag = true;
				break;
			}

		}
		if (!flag)
			((DrawerLayout) findViewById(R.id.main_drawer)).close();
	}

	/**
	 * Changes {@link AppCompatDelegate#setDefaultNightMode} to respective based on saved preference.
	 *
	 * @param context of activity/fragment.
	 * @see ApplicationSettings#getPreferenceValue
	 * @see SettingsFragment
	 */
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


	/**
	 * Is the main executor of forecast update.
	 * <ul>
	 *     <li>
	 *         Checks if device has Internet connection.
	 *     </li>
	 *     <li>
	 *        Calls API with provided {@link com.learning.sky.viewModel.CityAdapter.City},
	 *     </li>
	 *     <li>
	 *      Updates Side Nav
	 *     </li>
	 *     <li>
	 *      REWRITES file containing data
	 *     </li>
	 *
	 * </ul>
	 *
	 * @param city to perform API call with
	 * @see #updateSideNavChild
	 * @see #changeFragment
	 * @see FileOperator#writeFile
	 * @see WeatherFragment#populateForecast
	 * @see #isInternetAvailable()
	 */
	public void updateData(CityAdapter.City city) {
		if (isInternetAvailable()) {
			MainActivity.executor.execute(() -> {
				JsonObject callResult = WeatherAPI.call(city);
				executor.execute(() -> {
					WeatherFragment weatherFragment = WeatherFragment.newInstance();
					changeFragment(weatherFragment);
					handler.post(() ->
							weatherFragment.populateForecast(callResult)
					);
				});
				handler.post(() ->
						updateSideNavChild(callResult)
				);
				executor.execute(() ->
						FileOperator.writeFile(callResult)
				);
			});
		} else {
			handler.post(()->
				Toast.makeText(this, "Please Connect To Valid Network", Toast.LENGTH_LONG).show()
			);
		}

	}

	/**
	 * Checks whether device has access to the internet.
	 *
	 * @return true if there is access, false otherwise.
	 * @see #updateData
	 */
	public boolean isInternetAvailable() {
		try {
			InetAddress ipAddress = InetAddress.getByName("google.com");
			return !ipAddress.toString().equals("");
		} catch (Exception e) {
			return false;
		}
	}
}