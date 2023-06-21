package com.learning.sky;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SwitchCompat switchCompatSystemMode = view.findViewById(R.id.st_system_mode);
        SwitchCompat switchCompatDarkMode = view.findViewById(R.id.st_dark_mode);

        switchCompatSystemMode.setOnCheckedChangeListener((compoundButton, b) -> switchCompatDarkMode.setEnabled(switchCompatSystemMode.isChecked()));

        switchCompatDarkMode.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (switchCompatDarkMode.isChecked()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(view.getContext(), "Dark Mode Disabled", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(view.getContext(), "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        }));

        return view;
    }
}