package com.learning.sky.FragmentController;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.learning.sky.R;
import com.learning.sky.dao.FileOperator;
import com.learning.sky.dao.WeatherAPI;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherFragment extends Fragment implements View.OnClickListener {
	private static Random random;
	private static Executor executor;
	private static Handler handler = null;
	private View fragment;

	public WeatherFragment() {

	}

	public static WeatherFragment newInstance() {
		return new WeatherFragment();
	}

	public static void FillDataFromFile(String filename) {
		if (executor == null) {
			executor = Executors.newSingleThreadExecutor();
			handler = new Handler(Looper.getMainLooper());
		}
		executor.execute(() -> {
			JsonObject data = FileOperator.readFile(filename);
			handler.post(() -> {

			});
		});
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragment == null) {//			Initialize local variables if fragment is null (was not created)
			fragment = inflater.inflate(R.layout.fragment_weather, container, false);

			LinearLayout holder = (LinearLayout) fragment.findViewById(R.id.dailyForecast);
			for (int i = 0; i < holder.getChildCount(); i++) {
				holder.getChildAt(i).setOnClickListener(this);
			}


			SwipeRefreshLayout srl = fragment.findViewById(R.id.swipeRefreshLayout);
			handler = new Handler(Looper.getMainLooper());
			random = new Random();
			executor = Executors.newSingleThreadExecutor();

//			Initialize Logic for variables/Views
			srl.setOnRefreshListener(() -> {

				executor.execute(() -> {// BackGround thread (API CALL)
					JsonObject jsonObject = WeatherAPI.call("https://jsonplaceholder.typicode.com/posts/1");
					handler.post(() -> {// View thread
						Toast.makeText(fragment.getContext(), "Got Response For Title" + jsonObject.get("title").getAsString(), Toast.LENGTH_SHORT).show();
//						UpdateHourlyForecast();
//						UpdateDailyForecast();

						srl.setRefreshing(false);

					});
				});
			});
		}
		return fragment;
	}


	@SuppressLint("SetTextI18n")
	private void PopulateForecast() {
//		LinearLayout parent = fragment.findViewById(R.id.dailyForecast);
//		int childCount = parent.getChildCount();
//		for (int i = 0; i < childCount; i++) {
//			LinearLayout child = (LinearLayout) parent.getChildAt(i);
////            TextView day = (TextView) child.getChildAt(0);
//
////            ImageView weather_day = (ImageView) child.getChildAt(1);
////            ImageView weather_night = (ImageView) child.getChildAt(2);
//
//
//			TextView rain_percentage = (TextView) child.getChildAt(4);
//			rain_percentage.setText(random.nextInt(101) + "%");
//
//			TextView temperature_day = (TextView) child.getChildAt(5);
//			temperature_day.setText(random.nextInt(30) + "°");
//
//			TextView temperature_night = (TextView) child.getChildAt(child.getChildCount() - 1);
//			temperature_night.setText(random.nextInt(30) + "°");
//		}


	}


	@Override
	public void onClick(View view) {
		LinearLayout child = (LinearLayout) ((RelativeLayout) view).getChildAt(1);
		if (child.getVisibility() == View.VISIBLE) {
			child.setVisibility(View.GONE);
		} else {
			child.setVisibility(View.VISIBLE);
		}
	}
}