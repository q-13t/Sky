package com.learning.sky.FragmentController;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.learning.sky.MainActivity;
import com.learning.sky.R;
import com.learning.sky.dao.ApplicationSettings;

import org.jetbrains.annotations.Contract;


public class CitySearchFragment extends Fragment implements LocationListener {
	protected static Location location;
	protected static LocationManager locationManager;
	View fragment;
	private final CityAdapter adapter;


	public CitySearchFragment() {
		adapter = MainActivity.getAdapter();
	}


	@NonNull
	@Contract(" -> new")
	public static CitySearchFragment newInstance() {
		return new CitySearchFragment();
	}

	@Override
	public void onStop() {
		super.onStop();
		View view = requireActivity().getCurrentFocus();
		if (view != null) {
			((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		fragment = inflater.inflate(R.layout.fragment_city_search, container, false);

		fragment.findViewById(R.id.btn_current_location).setOnClickListener((View view) ->
				getLocation()
		);


		ListView list = fragment.findViewById(R.id.city_list);
		list.setTextFilterEnabled(true);
		MainActivity.executor.execute(
				() -> {

					MainActivity.handler.post(() -> {
						list.setAdapter(adapter);
					});
				}
		);


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

	public Location getLocation() {
		ApplicationSettings.checkOrRequestPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION, 200);
		if (locationManager == null) {
			locationManager = (LocationManager) fragment.getContext().getSystemService(Context.LOCATION_SERVICE);
		}
		Location gpslocation = null;
		Location networkLocation = null;
		try {
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				displayProviderAlert();
			}
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
				gpslocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			}
			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
				networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (gpslocation != null && networkLocation != null)
			return gpslocation.getTime() < networkLocation.getTime() ? gpslocation : networkLocation;
		else if (gpslocation == null)
			return networkLocation;
		else
			return gpslocation;
	}

	private void displayProviderAlert() {
		final String[] items = new String[]{"WIFI", "GPS", "CANCEL"};
		new AlertDialog.Builder(CitySearchFragment.this.requireContext())
				.setTitle("In order to use this function you need to have WIFI and/or GPS enabled.")
				.setCancelable(false)
				.setIcon(R.drawable.center_focus)
				.setItems(items, (dialog, which) -> {
					switch (which) {
						case 0: {
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
							dialog.cancel();
							break;
						}
						case 1: {
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							dialog.cancel();
							break;
						}
						case 2: {
							dialog.cancel();
							break;
						}
					}
				}).create().show();
	}

	@Override
	public void onProviderDisabled(@NonNull String provider) {
		if (isVisible()) {
			displayProviderAlert();
		}
	}


	@Override
	public void onLocationChanged(@NonNull Location Location) {
		if (location != Location) {
			location = Location;
			Toast.makeText(fragment.getContext(), "Longitude: " + location.getLongitude() + " Latitude: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
		}
	}
}

