package com.learning.sky.FragmentController;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.learning.sky.R;
import com.learning.sky.dao.FileOperator;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherFragment extends Fragment implements View.OnClickListener {

	private static Executor executor;
	private static Handler handler;
	private static WeakReference<View> fragment;

	public WeatherFragment() {

	}

	public static WeatherFragment newInstance() {
		return new WeatherFragment();
	}

	@SuppressLint("SetTextI18n")
	public static void PopulateForecast(JsonObject data) {
		((TextView) fragment.get().findViewById(R.id.city_name_banner)).setText(FileOperator.getCityName(data));

		LinearLayout fragmentContainer = fragment.get().findViewById(R.id.dailyForecast);//Fragment Weather Container
		JsonArray jsonArray = getAsJsonArray(data);

		for (int i = 0; i < Math.min(fragmentContainer.getChildCount(), jsonArray.size()); i++) {
			try{
				LinearLayout containerChildAt = (LinearLayout) fragmentContainer.getChildAt(i);//Fragment Weather Container Child
				LinearLayout weatherLinear = (LinearLayout) containerChildAt.getChildAt(0);// Fragment Forecast Daily Header
				JsonObject jsonElement = jsonArray.get(i).getAsJsonObject();

				((TextView) weatherLinear.getChildAt(0)).setText(getTime(jsonElement)); // Time
				((ImageView) weatherLinear.getChildAt(1)).setImageDrawable(fragment.get().getContext().getDrawable(getIconName(jsonElement))); //Icon
				// 	weatherLinear.getChildAt(2)//Rain Icon
				((TextView) weatherLinear.getChildAt(3)).setText(getRainPercentage(jsonElement) + "mm");//Rain Percentage
				((TextView) weatherLinear.getChildAt(4)).setText(getTempMax(jsonElement)+fragment.get().getContext().getString(R.string.degree_sign));//Temp Max
				((TextView) weatherLinear.getChildAt(5)).setText(getTempMin(jsonElement) + fragment.get().getContext().getString(R.string.degree_sign));//Temp min

				TableLayout weatherTable = (TableLayout) containerChildAt.getChildAt(1);

				((TextView) ((TableRow) weatherTable.getChildAt(0)).getChildAt(1)).setText(getPressure(jsonElement));//Pressure
				((TextView) ((TableRow) weatherTable.getChildAt(1)).getChildAt(1)).setText(getHumidity(jsonElement));//Humidity
				((TextView) ((TableRow) weatherTable.getChildAt(2)).getChildAt(1)).setText(getWind(jsonElement));//Wind
				((TextView) ((TableRow) weatherTable.getChildAt(3)).getChildAt(1)).setText(getVisibility(jsonElement));//Visibility
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private static String getPressure(JsonObject element) {
		return element.get("main").getAsJsonObject().get("pressure").getAsString();
	}

	private static String getHumidity(JsonObject element) {
		return element.get("main").getAsJsonObject().get("humidity").getAsString();
	}

	private static String getWind(JsonObject element) {
		JsonObject wind = element.get("wind").getAsJsonObject();
		return wind.get("speed").getAsString() +"("+ wind.get("deg").getAsString()+")";
	}

	private static String getVisibility(JsonObject element) {
		return element.get("visibility").getAsString()+"m";
	}

	private static String getTempMin(JsonObject element) {
		return element.get("main").getAsJsonObject().get("temp_min").getAsString();
	}

	private static String getTempMax(JsonObject element) {
		return element.get("main").getAsJsonObject().get("temp_max").getAsString();
	}

	private static String getRainPercentage(JsonObject element) {
		try {
			return element.get("rain").getAsJsonObject().get("3h").getAsString();
		} catch (Exception e) {
			return "0";
		}
	}

	public static JsonArray getAsJsonArray(JsonObject object) {
		return object.get("list").getAsJsonArray();
	}

	@SuppressLint("DefaultLocale")
	public static String getTime(JsonObject element) {
		return String.format("%02d", new Date(element.getAsJsonObject().get("dt").getAsLong() * 1000).getHours())+":00";
	}

	public static int getIconName(JsonObject element) {
		String ico = element.get("weather").getAsJsonArray().get(0).getAsJsonObject()
				.get("icon").getAsString();

		if (ico.equals("01d")) {
			return R.drawable.full_sun;
		} else if (ico.equals("01n")) {
			return R.drawable.full_moon;
		} else if (ico.equals("02d")) {
			return R.drawable.cloudy_sun;
		} else if (ico.equals("02n")) {
			return R.drawable.cloudy_moon;
		} else if (ico.matches("03.") || ico.matches("04.")) {
			return R.drawable.cloudy;
		} else if (ico.matches("09.") || ico.matches("10.")) {
			return R.drawable.rain;
		} else if (ico.matches("11.")) {
			return R.drawable.thunder;
		} else if (ico.matches("13.")) {
			return R.drawable.snow;
		} else if (ico.matches("50.")) {
			return R.drawable.mist;
		}

		return R.drawable.full_sun;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragment == null) {//			Initialize local variables if fragment is null (was not created)
			fragment =new WeakReference<>( inflater.inflate(R.layout.fragment_weather, container, false));

			LinearLayout holder = fragment.get().findViewById(R.id.dailyForecast);
			for (int i = 0; i < holder.getChildCount(); i++) {
				holder.getChildAt(i).setOnClickListener(this);
			}

			fragment.get().findViewById(R.id.expansion_btn).setOnClickListener((View view) -> {

			});


			SwipeRefreshLayout srl = fragment.get().findViewById(R.id.swipeRefreshLayout);
			handler = new Handler(Looper.getMainLooper());
			executor = Executors.newSingleThreadExecutor();

//			Initialize Logic for variables/Views
			srl.setOnRefreshListener(() -> {

				executor.execute(() -> {// BackGround thread (API CALL)
//					JsonObject jsonObject = WeatherAPI.call("https://jsonplaceholder.typicode.com/posts/1");
					handler.post(() -> {// View thread
//						Toast.makeText(fragment.getContext(), "Got Response For Title" + jsonObject.get("title").getAsString(), Toast.LENGTH_SHORT).show();
//						UpdateHourlyForecast();
//						UpdateDailyForecast();

						srl.setRefreshing(false);

					});
				});
			});
		}
		return fragment.get();
	}

	@Override
	public void onClick(View view) {
		LinearLayout child = (LinearLayout) ((LinearLayout) view).getChildAt(1);
		if (child.getVisibility() == View.VISIBLE) {
			child.setVisibility(View.GONE);
		} else {
			child.setVisibility(View.VISIBLE);
		}
	}
}