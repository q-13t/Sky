package com.learning.sky.FragmentController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.learning.sky.R;


public class CitySearchFragment extends Fragment {
	View fragment;

	public CitySearchFragment() {

	}


	public static CitySearchFragment newInstance() {
		return  new CitySearchFragment();
	}



	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		if(fragment==null){
			fragment = inflater.inflate(R.layout.fragment_city_search, container, false);

			fragment.findViewById(R.id.btn_current_location).setOnClickListener((View view)->{

			});
		}

		return fragment;
	}


}