package com.learning.sky.FragmentController;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.learning.sky.MainActivity;
import com.learning.sky.R;
import com.learning.sky.dao.FileOperator;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherFragment extends Fragment implements View.OnClickListener {

	private static Executor executor;
	private static Handler handler;
	public View fragment;

	public WeatherFragment() {
	}

	public static WeatherFragment newInstance() {
		return new WeatherFragment();
	}

	private static String getPressure(JsonObject element) {
		return element.get("main").getAsJsonObject().get("pressure").getAsString();
	}

	private static String getHumidity(JsonObject element) {
		return element.get("main").getAsJsonObject().get("humidity").getAsString();
	}

	private static String getWind(JsonObject element) {
		JsonObject wind = element.get("wind").getAsJsonObject();
		return wind.get("speed").getAsString() + "(" + wind.get("deg").getAsString() + ")";
	}

	private static String getVisibility(JsonObject element) {
		return element.get("visibility").getAsString() + "m";
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

	@NonNull
	public static String getTime(@NonNull JsonObject element) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(element.getAsJsonObject().get("dt").getAsLong() * 1000);
		return String.format(Locale.US,"%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":00";
	}

	public static int getIconName(@NonNull JsonObject element) {
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

	@SuppressLint("SetTextI18n")
	public void populateForecast(JsonObject data) {
		((TextView) fragment.findViewById(R.id.city_name_banner)).setText(FileOperator.getCityName(data));

		LinearLayout fragmentContainer = fragment.findViewById(R.id.dailyForecast);//Fragment Weather Container
		JsonArray jsonArray = getAsJsonArray(data);

		for (int i = 0; i < Math.min(fragmentContainer.getChildCount(), jsonArray.size()); i++) {
			try {
				LinearLayout containerChildAt = (LinearLayout) fragmentContainer.getChildAt(i);//Fragment Weather Container Child
				LinearLayout weatherLinear = (LinearLayout) containerChildAt.getChildAt(0);// Fragment Forecast Daily Header
				JsonObject jsonElement = jsonArray.get(i).getAsJsonObject();

				((TextView) weatherLinear.getChildAt(0)).setText(getTime(jsonElement)); // Time
//				((ImageView) weatherLinear.getChildAt(1)).setImageDrawable(fragment.getContext().getDrawable(getIconName(jsonElement))); //Icon
				((ImageView) weatherLinear.getChildAt(1)).setImageDrawable(AppCompatResources.getDrawable(fragment.getContext(), getIconName(jsonElement))); //Icon
				// 	weatherLinear.getChildAt(2)//Rain Icon
				((TextView) weatherLinear.getChildAt(3)).setText(getRainPercentage(jsonElement) + "mm");//Rain Percentage
				((TextView) weatherLinear.getChildAt(4)).setText(getTempMax(jsonElement) + fragment.getContext().getString(R.string.degree_sign));//Temp Max
				((TextView) weatherLinear.getChildAt(5)).setText(getTempMin(jsonElement) + fragment.getContext().getString(R.string.degree_sign));//Temp min

				TableLayout weatherTable = (TableLayout) containerChildAt.getChildAt(1);

				((TextView) ((TableRow) weatherTable.getChildAt(0)).getChildAt(1)).setText(getPressure(jsonElement));//Pressure
				((TextView) ((TableRow) weatherTable.getChildAt(1)).getChildAt(1)).setText(getHumidity(jsonElement));//Humidity
				((TextView) ((TableRow) weatherTable.getChildAt(2)).getChildAt(1)).setText(getWind(jsonElement));//Wind
				((TextView) ((TableRow) weatherTable.getChildAt(3)).getChildAt(1)).setText(getVisibility(jsonElement));//Visibility
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragment = inflater.inflate(R.layout.fragment_weather, container, false);

		LinearLayout holder = fragment.findViewById(R.id.dailyForecast);
		for (int i = 0; i < holder.getChildCount(); i++) {
			holder.getChildAt(i).setOnClickListener(this);
		}

		fragment.findViewById(R.id.expansion_btn).setOnClickListener((View view) -> {

		});


		SwipeRefreshLayout srl = fragment.findViewById(R.id.swipeRefreshLayout);
		handler = new Handler(Looper.getMainLooper());
		executor = Executors.newSingleThreadExecutor();

		srl.setOnRefreshListener(() -> executor.execute(() -> {// BackGround thread (API CALL)
			MainActivity.main.get().updateData(new CityAdapter.City(((TextView) fragment.findViewById(R.id.city_name_banner)).getText().toString()));
			handler.post(() -> {// View thread
				srl.setRefreshing(false);
			});
		})
		);

		return fragment;
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