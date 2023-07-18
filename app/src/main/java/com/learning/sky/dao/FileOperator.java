package com.learning.sky.dao;


import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.learning.sky.FragmentController.CityAdapter.City;
import com.learning.sky.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileOperator {
	private static final String TAG = "FileOperator";

	public static File getMainDir() {
		File mainDir = new File(MainActivity.main.get().getFilesDir(), "saved_data");
		if (!mainDir.exists())
			if (mainDir.mkdir())
				Log.i(TAG, "Unable to Create saved_data Directory!");
		return mainDir;
	}

	public static File[] listFiles() {
		return getMainDir().listFiles();
	}

	public static boolean writeFile(JsonObject data) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getMainDir(), getCityName(data))))) {
			bw.write(data.toString());
		} catch (IOException e) {
			e.getCause();
			return false;
		}
		return true;
	}

	public static String getCityName(JsonObject object) {
		return object.get("city").getAsJsonObject().get("name").getAsString();
	}

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

	public static void deleteFile(String fileName) {
		File[] files = getMainDir().listFiles((dir, name) -> name.equals(fileName));
		assert files != null;
		for (File file : files) {
			if (file.delete())
				Log.i(TAG, "Unable To Delete File" + file.getName());
		}
	}

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
			Log.e(TAG, "Error reading files",e.getCause());
			return null;
		}
		return cities;
	}
}
