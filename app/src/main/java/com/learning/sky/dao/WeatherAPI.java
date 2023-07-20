package com.learning.sky.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.learning.sky.FragmentController.CityAdapter.City;
import com.learning.sky.MainActivity;
import com.learning.sky.PreferenceType;
import com.learning.sky.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {
	private static final String TAG = "WeatherAPI";

	@NonNull
	private static String callAPI(String url) {
		StringBuilder response = new StringBuilder();
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
			} else {
				Log.e(TAG, "callAPI: Response Code not 200-> " + responseCode + " instead.");
			}
			connection.disconnect();
		} catch (IOException e) {
			Log.e(TAG, "callAPI: Error IN API call", e);
		}
		return response.toString();
	}


	public static JsonObject call(City city) {
		return new Gson().fromJson(WeatherAPI.callAPI(buildUrl(city)), JsonObject.class);
	}

	@NonNull
	private static String buildUrl(@NonNull City city) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.openweathermap.org/data/2.5/forecast?");
		if (city.getLat() == 0F || city.getLon() == 0F) {
			sb.append("q=").append(city.getName());
		} else {
			sb.append("lat=").append(city.getLat()).append("&").append("lon=").append(city.getLon());
		}
		sb.append("&units=");
		Boolean units = (Boolean) ApplicationSettings.getPreferenceValue(PreferenceType.BOOLEAN, MainActivity.main.get().getString(R.string.UNITS), MainActivity.main.get());
		if (Boolean.TRUE.equals(units)) {
			sb.append("metric");
		} else {
			sb.append("imperial");
		}
		sb.append("&appid=6fef81ad8239929ed64acc8700de9bce");
		Log.d(TAG, "buildUrl: " + sb);
		return sb.toString();
	}
}
