package com.learning.sky.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.learning.sky.R;
import com.learning.sky.view.MainActivity;
import com.learning.sky.viewModel.PreferenceType;

/**
 * Controls saved Preference's of application.
 */
public class ApplicationSettings extends MainActivity {

	/**
	 * Checks whether specified permission is granted.
	 * <p>
	 * If false asks user to grant.
	 * </p>
	 *
	 * @param context     of activity/fragment.
	 * @param permission  The requested permissions. Must be non-null and not empty.
	 * @param requestCode Application specific request code to match with a result reported to ActivityCompat.OnRequestPermissionsResultCallback.onRequestPermissionsResult(int, String[], int[]). Should be >= 0
	 */
	public static void checkOrRequestPermission(Context context, String permission, int requestCode) {
		if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions((AppCompatActivity) context, new String[]{permission}, requestCode);
		}
	}

	/**
	 * Based on {@link PreferenceType} provided will return corresponding preference or default value.
	 * <p style="color:green;">Note: if {@link PreferenceType} was not one of provided null will be returned</p>
	 *
	 * @param type           of {@link PreferenceType}
	 * @param preferenceName to be searched for.
	 * @param context        of activity/fragment.
	 * @return {@link Object} containing preference or default value.
	 */
	@Nullable
	public static Object getPreferenceValue(@NonNull PreferenceType type, String preferenceName, @NonNull Context context) {
		SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.PREFERENCES), MODE_PRIVATE);
		switch (type) {
			case STRING: {
				return sp.getString(preferenceName, "DEFAULT");
			}
			case BOOLEAN: {
				return sp.getBoolean(preferenceName, false);
			}
			case INTEGER: {
				return sp.getInt(preferenceName, -1);
			}
		}
		return null;
	}

	/**
	 * Writes applications preference with value and name.
	 *
	 * @param preferenceName to be paired with
	 * @param preference     the value itself
	 * @param context        of activity/fragment.
	 */
	public static void setPreferenceValue(String preferenceName, @NonNull Object preference, @NonNull Context context) {
		SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.PREFERENCES), 0).edit();
		Class<?> aClass = preference.getClass();
		if (Integer.class.equals(aClass)) {
			editor.putInt(preferenceName, (int) preference);
		} else if (String.class.equals(aClass)) {
			editor.putString(preferenceName, String.valueOf(preference));
		} else if (Boolean.class.equals(aClass)) {
			editor.putBoolean(preferenceName, (boolean) preference);
		}
		editor.apply();
	}

}
