package com.learning.sky.dao;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.learning.sky.MainActivity;
import com.learning.sky.PreferenceType;
import com.learning.sky.R;

public class ApplicationSettings extends MainActivity {

    public static Object getPreferenceValue(@NonNull PreferenceType type, String preferenceName , Context activity) {
        SharedPreferences sp = activity.getSharedPreferences(activity.getString(R.string.PREFERENCES), MODE_PRIVATE);
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

    public static void setPreferenceValue(String preferenceName, @NonNull Object preference, Context activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(activity.getString(R.string.PREFERENCES), 0).edit();
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
