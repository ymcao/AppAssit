package com.appassit.adapter;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.appassit.model.WeatherModel;
import com.appassit.widget.FutureWeatherItem;
import com.appassit.widget.FutureWeatherItem_;

@EBean
public class FutureWeatherAdapter extends BaseAdapter {

	@RootContext
	protected Context mContext;

	List<WeatherModel> mList = new ArrayList<WeatherModel>();

	public void appendToList(List<WeatherModel> list) {
		if (list == null) {
			return;
		}
		mList.clear();
		mList.addAll(list);
		try {
			notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<WeatherModel> getData() {
		return mList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WeatherModel model = mList.get(position);
		FutureWeatherItem item;

		if (convertView == null) {
			item = FutureWeatherItem_.build(mContext);
		} else {
			item = (FutureWeatherItem) convertView;
		}
		item.bind(model, position);
		return item;
	}

	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}
}
