package com.learning.sky.dao;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.learning.sky.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperator {

	public static File getMainDir() {
		File mainDir = new File(MainActivity.main.get().getFilesDir(), "saved_data");
		if (!mainDir.exists())
			mainDir.mkdir();
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


}
