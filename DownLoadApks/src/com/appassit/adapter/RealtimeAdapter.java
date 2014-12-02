package com.appassit.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.appassit.widget.RealtimeWeatherItem;
import com.appassit.widget.jazzyviewpager.JazzyViewPager;
import com.appassit.widget.jazzyviewpager.OutlineContainer;

/**
 */
public class RealtimeAdapter extends PagerAdapter {

	private final List<RealtimeWeatherItem> mList;
	private JazzyViewPager mViewPager;

	public RealtimeAdapter(List<RealtimeWeatherItem> list, JazzyViewPager viewPager) {
		mList = list;
		mViewPager = viewPager;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mList.get(position), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mViewPager.setObjectForPosition(mList.get(position), position);
		return mList.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mViewPager.findViewFromObject(position));
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		if (view instanceof OutlineContainer) {
			return ((OutlineContainer) view).getChildAt(0) == object;
		} else {
			return view == object;
		}
	}

}
