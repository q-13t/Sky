package com.learning.sky.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.learning.sky.FragmentController.CityAdapter.City;

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

	private static String buildUrl(City city) {
		return "https://api.openweathermap.org/data/2.5/forecast?" +
				"lat=" + city.getLat() + "&" +
				"lon=" + city.getLon() + "&" +
				"units=" + "metric" + "&" +// TODO: optimize units
				"appid=6fef81ad8239929ed64acc8700de9bce";
	}
}
