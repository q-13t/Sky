package com.learning.sky.viewModel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.learning.sky.view.MainActivity;
import com.learning.sky.R;
import com.learning.sky.model.ApplicationSettings;

import org.jetbrains.annotations.Contract;

/**
 * Fragment controlling class performing search for geolocation and/or search for city based on name provided.
 */
public class CitySearchFragment extends Fragment implements LocationListener {
	private static final String TAG = "CitySearchFragment";
	protected static Location location;
	protected static LocationManager locationManager;
	View fragment;
	private boolean called;
	private CityAdapter adapter;
	private ListView list;

	public CitySearchFragment() {
		adapter = MainActivity.getAdapter();
	}

	@NonNull
	@Contract(" -> new")
	public static CitySearchFragment newInstance() {
		return new CitySearchFragment();
	}

	public void onAdapterReady(CityAdapter adapter) {
		this.adapter = adapter;
		requireActivity().runOnUiThread(() -> {
			if (list.getAdapter() == null || list.getAdapter().isEmpty()) list.setAdapter(adapter);
		});
	}

	/**
	 * Clears {@linkplain #adapter} and attempts to close keyboard if present.
	 *
	 * @see CityAdapter#clear()
	 */
	@Override
	public void onStop() {
		if (adapter != null) adapter.clear();
		super.onStop();
		View view = requireActivity().getCurrentFocus();
		if (view != null) {
			((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * Setts all the listeners and initial settings for CitySearch fragment.
	 *
	 * @see MainActivity#getAdapter()
	 * @see CityAdapter
	 */
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		fragment = inflater.inflate(R.layout.fragment_city_search, container, false);

		fragment.findViewById(R.id.btn_current_location).setOnClickListener((View view) -> {
			called = false;
			getLocation();
		});

		list = fragment.findViewById(R.id.city_list);
		list.setTextFilterEnabled(true);
		if (adapter != null) {
			list.setAdapter(adapter);
			list.setOnItemClickListener(adapter);
		}

		SearchView searchView = fragment.findViewById(R.id.autoCompleteCityName);
		searchView.setQueryHint(getString(R.string.city_name_hint));
		searchView.setIconified(false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.isEmpty()) adapter.clear();
				else if (adapter != null) adapter.getFilter().filter(newText);
				return true;
			}
		});
		return fragment;
	}

	/**
	 * <p>
	 * Is called by press of geolocation button in CitySearch fragment.
	 * </p>
	 * <p>
	 * Checks if {@linkplain Manifest.permission#ACCESS_FINE_LOCATION} is granted using {@link ApplicationSettings#checkOrRequestPermission}.
	 * Then checks if {@link  LocationManager#GPS_PROVIDER} or {@link LocationManager#NETWORK_PROVIDER} is/are turned off, if so {@link #displayProviderAlert()} is called.
	 * Otherwise {@link #locationManager} start listening for location updates.
	 *
	 * @see #displayProviderAlert()
	 * @see ApplicationSettings#checkOrRequestPermission
	 */
	public void getLocation() {
		ApplicationSettings.checkOrRequestPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION, 200);
		if (locationManager == null) {
			locationManager = (LocationManager) fragment.getContext().getSystemService(Context.LOCATION_SERVICE);
		}
		try {
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				displayProviderAlert();
			} else {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays {@linkplain AlertDialog} to redirect user to gps settings for geolocation to be tuned on.
	 *
	 * @see #onProviderDisabled
	 */
	private void displayProviderAlert() {
		new AlertDialog.Builder(CitySearchFragment.this.requireContext()).setMessage("In order to use this function you need to have GPS enabled.").setIcon(R.drawable.center_focus).setPositiveButton("Go to settings", (dialog, which) -> {
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			dialog.cancel();
		}).setNegativeButton("Cancel", ((dialog, which) -> dialog.cancel())).create().show();
	}

	/**
	 * <p>
	 * Is called If {@link  LocationManager#GPS_PROVIDER} or {@link LocationManager#NETWORK_PROVIDER} is/are turned off.
	 * </p>
	 * Displays {@link #displayProviderAlert()} and logs situation.
	 *
	 * @param provider the name of the location provider
	 * @see #getLocation()
	 */
	@Override
	public void onProviderDisabled(@NonNull String provider) {
		Log.i(TAG, "onProviderDisabled: " + provider + " turned OFF");
		displayProviderAlert();
	}

	/**
	 * Listener For Location Change.
	 * When location is changed/updated {@link MainActivity#updateData} is called.
	 *
	 * @param Location the updated location
	 * @see MainActivity#updateData
	 */
	@Override
	public void onLocationChanged(@NonNull Location Location) {
		if (location != Location) {
			location = Location;
		}
		if (!called) {
			MainActivity.main.get().updateData(new CityAdapter.City((float) location.getLongitude(), (float) location.getLatitude()));
			called = true;
		}
	}
}

