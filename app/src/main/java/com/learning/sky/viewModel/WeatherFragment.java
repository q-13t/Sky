package com.learning.sky.viewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.learning.sky.R;
import com.learning.sky.view.MainActivity;

import org.jetbrains.annotations.Contract;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * Fragment controlling class that is responsible for displaying weather forecast.
 */
public class WeatherFragment extends Fragment implements View.OnClickListener {
	public View fragment;


	/**
	 * @return new instance of WeatherFragment class.
	 */
	@NonNull
	@Contract(" -> new")
	public static WeatherFragment newInstance() {
		return new WeatherFragment();
	}

	/**
	 * @param element of the weather forecast.
	 * @return String representing pressure.
	 */
	private static String getPressure(@NonNull JsonObject element) {
		return element.get("main").getAsJsonObject().get("pressure").getAsString();
	}

	/**
	 * @param element of the weather forecast.
	 * @return String representing humidity.
	 */
	private static String getHumidity(@NonNull JsonObject element) {
		return element.get("main").getAsJsonObject().get("humidity").getAsString();
	}

	/**
	 * @param object of the weather forecast.
	 * @return String containing city name.
	 */
	public static String getCityName(@NonNull JsonObject object) {
		return object.get("city").getAsJsonObject().get("name").getAsString();
	}

	/**
	 * @param element of the weather forecast.
	 * @return String containing wind data in format "speed(degree)"
	 */
	@NonNull
	private static String getWind(@NonNull JsonObject element) {
		JsonObject wind = element.get("wind").getAsJsonObject();
		return wind.get("speed").getAsString() + "(" + wind.get("deg").getAsString() + ")";
	}

	/**
	 * @param element of the weather forecast.
	 * @return String containing visibility (represented in meters).
	 */
	@NonNull
	private static String getVisibility(@NonNull JsonObject element) {
		return element.get("visibility").getAsString() + "m";
	}

	/**
	 * @param element of the weather forecast.
	 * @return String with float value of minimum temperature in given time.
	 * @see #getTempMax
	 */
	private static String getTempMin(@NonNull JsonObject element) {
		return element.get("main").getAsJsonObject().get("temp_min").getAsString();
	}

	/**
	 * @param element of the weather forecast.
	 * @return String with float value of maximum temperature in given time.
	 * @see #getTempMin
	 */
	private static String getTempMax(@NonNull JsonObject element) {
		return element.get("main").getAsJsonObject().get("temp_max").getAsString();
	}

	/**
	 * @param element of the weather forecast.
	 * @return String representing rain percentage.
	 */
	private static String getRainPercentage(JsonObject element) {
		try {
			return element.get("rain").getAsJsonObject().get("3h").getAsString();
		} catch (Exception e) { // If any Exception occurs return 0 for display
			return "0";
		}
	}

	/**
	 * @param object of the weather forecast.
	 * @return {@link JsonArray} with forecast for each hour.
	 */
	public static JsonArray getAsJsonArray(@NonNull JsonObject object) {
		return object.get("list").getAsJsonArray();
	}

	/**
	 * @param element of the weather forecast.
	 * @return String containing time in format "00:00".
	 */
	@NonNull
	public static String getTime(@NonNull JsonObject element) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(element.getAsJsonObject().get("dt").getAsLong() * 1000);
		return String.format(Locale.US, "%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":00";
	}

	/**
	 * @param element of the weather forecast.
	 * @return id of corresponding sky state image.
	 */
	public static int getIconID(@NonNull JsonObject element) {
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

	/**
	 * Sets content for Weather fragment.
	 *
	 * @param data that will be displayed.
	 */
	@SuppressLint("SetTextI18n")
	public void populateForecast(JsonObject data) {

			SwipeRefreshLayout SRL = fragment.findViewById(R.id.swipeRefreshLayout);
			((TextView) fragment.findViewById(R.id.city_name_banner)).setText(getCityName(data));
			LinearLayout fragmentContainer = fragment.findViewById(R.id.dailyForecast);//Fragment Weather Container
			JsonArray jsonArray = getAsJsonArray(data);

			for (int i = 0; i < Math.min(fragmentContainer.getChildCount(), jsonArray.size()); i++) {
				try {
					LinearLayout containerChildAt = (LinearLayout) fragmentContainer.getChildAt(i);//Fragment Weather Container Child
					LinearLayout weatherLinear = (LinearLayout) containerChildAt.getChildAt(0);// Fragment Forecast Daily Header
					JsonObject jsonElement = jsonArray.get(i).getAsJsonObject();

					((TextView) weatherLinear.getChildAt(0)).setText(getTime(jsonElement)); // Time
					((ImageView) weatherLinear.getChildAt(1)).setImageDrawable(AppCompatResources.getDrawable(fragment.getContext(), getIconID(jsonElement))); //Icon
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
			SRL.setRefreshing(false);
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragment = inflater.inflate(R.layout.fragment_weather, container, false);

		LinearLayout holder = fragment.findViewById(R.id.dailyForecast);
		for (int i = 0; i < holder.getChildCount(); i++) {
			holder.getChildAt(i).setOnClickListener(this);
		}

		fragment.findViewById(R.id.expansion_btn).setOnClickListener((View view) -> expandAllForecasts((Button) view));

		SwipeRefreshLayout srl = fragment.findViewById(R.id.swipeRefreshLayout);
		MainActivity.handler = new Handler(Looper.getMainLooper());
		MainActivity.executor = Executors.newSingleThreadExecutor();

		srl.setOnRefreshListener(() -> MainActivity.executor.execute(() -> {// BackGround thread (API CALL)
					MainActivity.main.get().updateData(new CityAdapter.City(((TextView) fragment.findViewById(R.id.city_name_banner)).getText().toString()));
					srl.setRefreshing(true);
				})
		);
		return fragment;
	}

	/**
	 * <p>
	 * Support function for "Expansion" button.
	 * </p>
	 * Collapses or Expends all forecast details depending on text within button itself.
	 * Changes buttons text from "Expand" to "Collapse" and wise-versa.
	 *
	 * @param button Original button that was clicked.
	 */
	private void expandAllForecasts(@NonNull Button button) {
		LinearLayout linearLayout = fragment.findViewById(R.id.dailyForecast);

		if (button.getText().equals(getString(R.string.Expand))) {
			button.setText(getString(R.string.Collapse));
			MainActivity.handler.post(() -> {
				for (int i = 0; i < linearLayout.getChildCount(); i++) {
					((LinearLayout) linearLayout.getChildAt(i)).getChildAt(1).setVisibility(View.VISIBLE);
				}
			});
		} else {
			button.setText(getString(R.string.Expand));
			MainActivity.handler.post(() -> {
				for (int i = 0; i < linearLayout.getChildCount(); i++) {
					((LinearLayout) linearLayout.getChildAt(i)).getChildAt(1).setVisibility(View.GONE);
				}
			});
		}
	}

	/**
	 * <p>
	 * Listener for each element displaying forecast in fragment.
	 * </p>
	 * If element is expended collapses it, expends otherwise.
	 *
	 * @param view The view that was clicked.
	 */
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