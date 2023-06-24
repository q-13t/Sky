package com.learning.sky.dao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {


	public static String executeExample(String url) {
		StringBuilder response = new StringBuilder();
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// Read and process the response
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				// Handle the response as needed
			} else {
				System.out.println("ERROR IN API CALL");
			}
			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response.toString();
	}

	/**
	 * Performs API call to specified url
	 *
	 * @param params to be passed with call
	 * @param url    to perform call to
	 * @return {@link JsonObject}
	 */
	public static JsonObject call(String url, String... params) {
		return new Gson().fromJson(WeatherAPI.executeExample(url), JsonObject.class);
	}
}
