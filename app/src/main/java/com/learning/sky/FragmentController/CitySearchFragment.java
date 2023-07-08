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
		if (fragment == null) {
			fragment = inflater.inflate(R.layout.fragment_city_search, container, false);

			fragment.findViewById(R.id.btn_current_location).setOnClickListener((View view) ->
					getLocation()
			);
		}

		return fragment;
	}

	public Location getLocation() {
		ApplicationSettings.checkOrRequestPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION, 200);
		if (locationManager == null) {
			locationManager = (LocationManager) fragment.getContext().getSystemService(Context.LOCATION_SERVICE);
		}
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1000, this);
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		} else {
			displayProviderAlert();
		}
		if (location != null) {
			Toast.makeText(fragment.getContext(), "Longitude: " + location.getLongitude() + " Latitude: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
		}
		return location;
	}

	private void displayProviderAlert() {
		new AlertDialog.Builder(CitySearchFragment.this.requireContext()).setMessage("Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("OK", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
				.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.cancel()).create().show();
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