package com.learning.sky.dao;


import android.content.Context;
import android.content.Intent;

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
		File mainDir = MainActivity.main.getFilesDir();
		if (!mainDir.exists())
			mainDir.mkdir();
		return mainDir;
	}

	public static File[] listFiles(){
		return  getMainDir().listFiles();
	}

	public static boolean writeFile(String data, String filename) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getMainDir(), filename)))) {
			bw.write(data);
		} catch (IOException e) {
			e.getCause();
			return false;
		}
		return true;
	}

	public static JsonObject readFile(String filename) {
		StringBuilder result = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(getMainDir(), filename)))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			e.getCause();
			return null;
		}
		return new Gson().fromJson(result.toString(),JsonObject.class);
	}

}
