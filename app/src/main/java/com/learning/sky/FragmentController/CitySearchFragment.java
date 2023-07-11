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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.learning.sky.R;
import com.learning.sky.dao.ApplicationSettings;

import org.jetbrains.annotations.Contract;


public class CitySearchFragment extends Fragment implements LocationListener {
	protected static Location location;
	protected static LocationManager locationManager;
	View fragment;

	public CitySearchFragment() {
	}


	@NonNull
	@Contract(" -> new")
	public static CitySearchFragment newInstance() {
		return new CitySearchFragment();
	}


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		fragment = inflater.inflate(R.layout.fragment_city_search, container, false);

		fragment.findViewById(R.id.btn_current_location).setOnClickListener((View view) ->
				getLocation()
		);
		fragment.findViewById(R.id.autoCompleteCityName).setOnClickListener((View view) -> {

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