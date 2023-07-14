package com.learning.sky.FragmentController;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.learning.sky.R;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends BaseAdapter implements Filterable {
	public Context context;
	public List<City> cities;
	public List<City> filtered;

	public CityAdapter(Context context, List<City> cities) {
		super();
		this.context = context;
		this.cities = cities;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CityHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
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
			protected void publishResults(CharSequence constraint, FilterResults results) {
				filtered = (List<City>) results.values;
				notifyDataSetChanged();
			}
		};
	}

	public void clear() {
		filtered.clear();
		notifyDataSetChanged();
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

	public static class CityHolder {
		TextView name;
		TextView country;
	}
}
