package com.learning.sky.viewModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.learning.sky.view.MainActivity;
import com.learning.sky.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for {@link android.widget.ListView} within {@link CitySearchFragment}.
 */
public class CityAdapter extends BaseAdapter implements Filterable, AdapterView.OnItemClickListener {
	public static List<City> cities;
	public final WeakReference<Context> context;
	public List<City> filtered;

	/**
	 * Setts initial data for adapter.
	 * @param context of the activity
	 * @param citiesList List containing data of cities.
	 */
	public CityAdapter(Context context, List<City> citiesList) {
		super();
		cities = citiesList;
		this.context = new WeakReference<>(context);
		this.filtered = new ArrayList<>();
	}

	@Override
	public int getCount() {
		return filtered.size();
	}

	@Override
	public City getItem(int position) {
		return filtered.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Creates or alters matching results from {@link #getFilter()} to Views containing City Name and Country.
	 * @return {@link View} for each filtered result.
	 * @see CityHolder
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CityHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context.get()).inflate(R.layout.list_item, parent, false);
			holder = new CityHolder();
			LinearLayout listItem = convertView.findViewById(R.id.list_item);
			holder.name = (TextView) listItem.getChildAt(0);
			holder.country = (TextView) listItem.getChildAt(1);
			convertView.setTag(holder);
		} else {
			holder = (CityHolder) convertView.getTag();
		}
		holder.name.setText(filtered.get(position).getName());
		holder.country.setText(filtered.get(position).getCountry());
		return convertView;
	}

	/**
	 * Filters Cities on provided in {@link CitySearchFragment} city name data.
	 * @return filter for {@link android.widget.ListView}.
	 */
	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				ArrayList<City> results = new ArrayList<>();
				if (constraint != null) {
					if (cities != null && cities.size() > 0) {
						for (City city : cities) {
							if (city.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
								results.add(city);
						}
					}
					filterResults.values = results;
				}
				return filterResults;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint, FilterResults results) {
				filtered = (List<City>) results.values;
				notifyDataSetChanged();
			}
		};
	}

	/**
	 * Clears {@link #getFilter()} results and notifies view to update.
	 */
	public void clear() {
		filtered.clear();
		notifyDataSetChanged();
	}

	/**
	 * Calls {@link MainActivity#updateData} with {@link City} on selected position.
	 * @see #getItem
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MainActivity.main.get().updateData(getItem(position));
	}

	@SuppressWarnings("unused")
	public static class City implements Comparable<City> {
		private float lat;
		private float lon;
		private String name;
		private String country;

		public City(float lat, float lon, String name, String country) {
			this.lat = lat;
			this.lon = lon;
			this.name = name;
			this.country = country;
		}

		public City(String name) {
			this.name = name;
		}

		public City(float longitude, float latitude) {
			this.lat = latitude;
			this.lon = longitude;
		}

		@NonNull
		@Override
		public String toString() {
			return "\nCity[ Name: " + name + ", Country: " + country + ", Latitude: " + lat + ", Longitude: " + lon + "]";
		}

		public float getLat() {
			return lat;
		}

		public void setLat(int lat) {
			this.lat = lat;
		}

		public float getLon() {
			return lon;
		}

		public void setLon(int lon) {
			this.lon = lon;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		@Override
		public int compareTo(City to) {
			return to.name.compareTo(this.name);
		}

	}

	/**
	 * <p>
	 *     Holds 2 {@link TextView}s for {@link #getView} to be displayed.
	 * </p>
	 * Contains {@link City#name} and {@link City#country}.
	 */
	public static class CityHolder {
		TextView name;
		TextView country;
	}
}
