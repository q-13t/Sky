package com.learning.sky.model;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.learning.sky.view.MainActivity;
import com.learning.sky.viewModel.CityAdapter.City;
import com.learning.sky.viewModel.WeatherFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Performs basic operations on file system of application.
 */
public class FileOperator {
	private static final String TAG = "FileOperator";

	/**
	 * Checks if "saved_data" directory exists. If not creates it and returns.
	 *
	 * @return File variable related to "saved_data" directory.
	 */
	@NonNull
	public static File getMainDir() {
		File mainDir = new File(MainActivity.main.get().getFilesDir(), "saved_data");
		if (!mainDir.exists())
			if (mainDir.mkdir())
				Log.i(TAG, "Unable to Create saved_data Directory!");
		return mainDir;
	}

	/**
	 * @return File array with relative URIs to all (or none) files within "saved_data" directory.
	 * @see #getMainDir()
	 */
	public static File[] listFiles() {
		return getMainDir().listFiles();
	}

	/**
	 * Writes forecast data to "saved_data" directory.
	 * <p >
	 * Note: Files with same name as in data <span style="color:red;">WILL BE OVERRIDDEN</span>.
	 * </p>
	 *
	 * @param data to be written.
	 * @see #getMainDir()
	 */
	public static void writeFile(@NonNull JsonObject data) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getMainDir(), WeatherFragment.getCityName(data))))) {
			bw.write(data.toString());
		} catch (IOException e) {
			e.getCause();
		}
	}

	/**
	 * @param filename name of file to be read.
	 * @return {@link JsonObject} containing files data.
	 * @see #getMainDir()
	 */
	@Nullable
	public static JsonObject readFile(String filename) {
		StringBuilder result = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(getMainDir(), filename)))) {
			String line;
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			e.getCause();
			return null;
		}
		return new Gson().fromJson(result.toString(), JsonObject.class);
	}

	/**
	 * @param fileName to be deleted from "saved_data" directory.
	 * @see #getMainDir()
	 */
	public static void deleteFile(String fileName) {
		File[] files = getMainDir().listFiles((dir, name) -> name.equals(fileName));
		assert files != null;
		for (File file : files) {
			if (file.delete())
				Log.i(TAG, "Unable To Delete File" + file.getName());
		}
	}

	/**
	 * Reads ALL Cities contained in "world-cities.csv" file.
	 * <p>
	 * Note: there are 44691 cities within that file.
	 * </p>
	 *
	 * @return ArrayList containing all cities.
	 */
	@Nullable
	public static ArrayList<City> readCities() {
		ArrayList<City> cities = new ArrayList<>(44692);
		try (BufferedReader br = new BufferedReader(new FileReader(new File(MainActivity.main.get().getFilesDir(), "world-cities.csv")))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",");
				cities.add(new City(Float.parseFloat(fields[1]), Float.parseFloat(fields[2]), fields[0], fields[3]));
			}
		} catch (IOException e) {
			Log.e(TAG, "Error reading files", e.getCause());
			return null;
		}
		return cities;
	}
}
