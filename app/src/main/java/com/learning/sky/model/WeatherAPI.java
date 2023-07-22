package com.learning.sky.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.learning.sky.R;
import com.learning.sky.view.MainActivity;
import com.learning.sky.viewModel.CityAdapter.City;
import com.learning.sky.viewModel.PreferenceType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * API based class that performs URL building and API call.
 */
public class WeatherAPI {
	private static final String TAG = "WeatherAPI";

	/**
	 * Performs API call to URL built by {@link #buildUrl}.
	 *
	 * @param url to perform call to
	 * @return String value of response.
	 * @see #call
	 * @see #buildUrl
	 */
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

	/**
	 * <p>
	 * Handler function that is connector between {@link #buildUrl} and {@link #callAPI}  functions.
	 * </p>
	 * Should be called instead of  {@link #buildUrl} and {@link #callAPI}.
	 *
	 * @param city to build API URL based on.
	 * @return {@link JsonObject} containing weather forecast.
	 */
	public static JsonObject call(City city) {
		return new Gson().fromJson(WeatherAPI.callAPI(buildUrl(city)), JsonObject.class);
	}

	/**
	 * <p>
	 * Build API URL to be called based on {@link City} provided.
	 * </p>
	 * Will attempt to build URL sorely on city name. If not possible Longitude and Latitude will be used.
	 *
	 * @param city to build API URL based on.
	 * @return String containing URL.
	 */
	@NonNull
	private static String buildUrl(@NonNull City city) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.openweathermap.org/data/2.5/forecast?");
		if (city.getLat() == 0F || city.getLon() == 0F || !city.getName().isEmpty()) {
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
