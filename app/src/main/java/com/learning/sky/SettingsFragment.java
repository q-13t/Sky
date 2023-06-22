package com.learning.sky;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.learning.sky.dao.ApplicationSettings;

import java.util.Objects;

public class SettingsFragment extends Fragment {
    View fragment;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragment == null) {
            fragment = inflater.inflate(R.layout.fragment_settings, container, false);

            SwitchCompat switchCompatSystemMode = fragment.findViewById(R.id.st_system_mode);
            SwitchCompat switchCompatDarkMode = fragment.findViewById(R.id.st_dark_mode);
            MainActivity main = new MainActivity();


            if(Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.BOOLEAN, getString(R.string.CUSTOM_MODE), requireActivity()), false)){
                switchCompatSystemMode.setChecked(false);
                switchCompatDarkMode.setEnabled(false);
            }else{
                switchCompatSystemMode.setChecked(true);
            }
            switchCompatDarkMode.setChecked(Objects.equals(ApplicationSettings.getPreferenceValue(PreferenceType.BOOLEAN, getString(R.string.DARK_MODE), requireActivity()), true));

            switchCompatSystemMode.setOnCheckedChangeListener((compoundButton, b) -> {
                if(switchCompatSystemMode.isChecked()){
                    ApplicationSettings.setPreferenceValue(getString(R.string.CUSTOM_MODE),true, requireActivity());
                    switchCompatDarkMode.setEnabled(true);
                }else{
                    ApplicationSettings.setPreferenceValue(getString(R.string.CUSTOM_MODE),false, requireActivity());
                    switchCompatDarkMode.setEnabled(false);
                }
                main.repaint(requireActivity());
            });

            switchCompatDarkMode.setOnCheckedChangeListener(((compoundButton, b) -> {
                if (switchCompatDarkMode.isChecked()) {
                    ApplicationSettings.setPreferenceValue(getString(R.string.DARK_MODE),true, requireActivity());
                } else {
                    ApplicationSettings.setPreferenceValue(getString(R.string.DARK_MODE),false, requireActivity());
                }
               main.repaint(requireActivity());
            }));
        }


        return fragment;
    }
}