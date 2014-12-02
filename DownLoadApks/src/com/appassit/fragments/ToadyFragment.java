package com.appassit.fragments;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appassit.R;
import com.appassit.activitys.CityActivity_;
import com.appassit.activitys.SLAppication;
import com.appassit.adapter.FutureWeatherAdapter;
import com.appassit.adapter.RealtimeAdapter;
import com.appassit.common.Const;
import com.appassit.data.GsonWeatherRequest;
import com.appassit.data.RequestManager;
import com.appassit.db.SunDataHelper;
import com.appassit.model.SunModel;
import com.appassit.model.city.AreaModel;
import com.appassit.service.WeatherUpdateService;
import com.appassit.tools.NetUtil;
import com.appassit.tools.SharedPrefUtil;
import com.appassit.widget.RealtimeWeatherItem;
import com.appassit.widget.RealtimeWeatherItem_;
import com.appassit.widget.jazzylistview.JazzyListView;
import com.appassit.widget.jazzyviewpager.JazzyViewPager;

@EFragment(R.layout.fragment_today_weather)
public class ToadyFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
	/** 是否第一次进入本Fragment */
	protected boolean isFrastEnter = true;
	protected final static String TAG = "HomeFragment";
	private SunDataHelper mSunDB;

	@ViewById(R.id.tv_city)
	TextView tvCity;

	@ViewById(R.id.tv_temp)
	TextView tvTemp;

	@ViewById(R.id.tv_weather)
	TextView tvWeather;

	@AnimationRes
	Animation fadeIn;

	@ViewById(R.id.swipe_layout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	private MenuItem mRefreshItem;

	/** 是否是获取所有城市数据 */
	private boolean isGetAllData;

	/** 今日天气Pager */
	@ViewById(R.id.jazzy_pager)
	JazzyViewPager mViewPager;

	/** 所有城市天气数据 */
	List<SunModel> mSunModels;
	List<RealtimeWeatherItem> mRealtimeViews;

	/** 未来天气适配器 */
	@Bean
	FutureWeatherAdapter mFutureWeatherAdapter;

	/** 未来天气ListView */
	@ViewById(R.id.lv_weather)
	JazzyListView mFutureWeatherListView;

	/** 当前Page页面 */
	private int mCurPageIndex;
	/** 城市数 */
	private int mCityNum;

	@AfterViews
	void initFragment() {
		isFrast();
		setHasOptionsMenu(true);
		initData();
		initView();
	}

	private void isFrast() {
		if (SharedPrefUtil.isFrast()) {
			// 进入选择城市
			CityActivity_.intent(this).startForResult(RealtimeWeatherItem.REQUEST_CODE);
			SharedPrefUtil.setFrast();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mViewPager.setCurrentItem(SLAppication.getCurCityIndex());
	}

	private void initData() {
		mCityNum = SLAppication.getMyArea().size();
		mSunDB = new SunDataHelper(SLAppication.getContext());
		mSunModels = new ArrayList<SunModel>();
		// 获取所有城市天气
		if (NetUtil.isNetworkConnected()) {
			getAllWeatherData();
		} else {
			getDataFromDB();
		}
	}

	private void initView() {
		mFutureWeatherListView.setAdapter(mFutureWeatherAdapter);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
	}

	private void initViewPager() {
		mRealtimeViews = new ArrayList<RealtimeWeatherItem>();
		for (int i = 0; i < mSunModels.size(); i++) {
			RealtimeWeatherItem item = RealtimeWeatherItem_.build(getActivity(), this);
			item.bind(mSunModels.get(i).realtime);
			mRealtimeViews.add(item);
		}
		mViewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.Accordion);
		mViewPager.setOnPageChangeListener(new MyPageViewListener());
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setAdapter(new RealtimeAdapter(mRealtimeViews, mViewPager));
		mViewPager.setPageMargin(3);
		mCurPageIndex = mViewPager.getCurrentItem();
	}

	/**
	 * 从数据库中获取数据
	 */
	@Background
	void getDataFromDB() {
		for (AreaModel model : SLAppication.getMyArea()) {
			SunModel sunModel = mSunDB.query(model.getWeatherCode());
			if (sunModel != null) {
				mSunModels.add(sunModel);
			}
		}
		// 更新
		updateView();
	}

	/**
	 * 插入数据到数据库
	 */
	@Background
	void inserData() {
		mSunDB.deleteAll();
		mSunDB.bulkInsert(mSunModels);
	}

	private void onRefreshData() {
		getCityWeatherData(SLAppication.getMyArea().get(mCurPageIndex).getWeatherCode());
	}

	/**
	 * 获取所有城市天气
	 */
	void getAllWeatherData() {
		isGetAllData = true;
		// 清除所有数据
		setRefreshing(true);
		mSunModels.clear();
		getAllWeather();
	}

	@Background
	void getAllWeather() {
		// 获取数据
		for (int i = 0; i < SLAppication.getMyArea().size(); i++) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			getWeatherData(SLAppication.getMyArea().get(i).getWeatherCode());
		}
	}

	// weather code:101021300
	/**
	 * 获取某一城市天气
	 * 
	 * @param id
	 */
	private void getCityWeatherData(String id) {
		isGetAllData = false;
		getWeatherData(id);
	}

	/**
	 * 更新页面
	 */
	private void updateView() {

		// 更新每小时天气
		/*
		 * if (activity != null) {
		 * activity.updateHourly(mSunModels.get(mCurPageIndex).hourlyForecast);
		 * }
		 */
		// 更新ListView
		if (mSunModels != null && mSunModels.size() > 0) {
			mFutureWeatherAdapter.appendToList(mSunModels.get(mCurPageIndex).weather);
		}
		// 更新ViewPager
		initViewPager();
		// 插入数据到数据库里
		inserData();
		setRefreshing(false);
	}

	/**
	 * 获取城市信息
	 * 
	 * @param id
	 *            天气WeatherCode
	 */
	@SuppressWarnings("unchecked")
	private void getWeatherData(String id) {
		executeRequest(new GsonWeatherRequest<SunModel.SunRequestData>(Const.WEATER_INFO_URL + "?app=tq360&_jsonp=renderData&code=" + id,
				SunModel.SunRequestData.class, responseListener(), errorListener()));
	}

	protected void executeRequest(Request<?> request) {
		RequestManager.addRequest(request, this);
	}

	/**
	 * 获取数据成功回调
	 * 
	 * @return
	 */
	private Response.Listener<SunModel.SunRequestData> responseListener() {
		return new Response.Listener<SunModel.SunRequestData>() {
			@Override
			public void onResponse(SunModel.SunRequestData sunRequestData) {
				sunRequestData.data.id = sunRequestData.data.area[2][1];
				sunRequestData.data.realtime.cityName = sunRequestData.data.area[2][0];
				// 将请求到的数据加入到List中
				if (!isGetAllData) {
					mSunModels.remove(mCurPageIndex);
					mSunModels.add(mCurPageIndex, sunRequestData.data);
				} else {
					mSunModels.add(sunRequestData.data);
				}
				// 判断当前的数据是否已经全部获取到，来更新界面
				if (!isGetAllData || mSunModels.size() == SLAppication.getMyArea().size()) {
					updateView();
				}
			}
		};
	}

	/**
	 * 获取数据错误回调
	 * 
	 * @return
	 */
	protected Response.ErrorListener errorListener() {

		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				volleyError.printStackTrace();
				// 让刷新进度环停止
				setRefreshing(false);
			}
		};
	}

	@OnActivityResult(RealtimeWeatherItem.REQUEST_CODE)
	void onResult(int resultCode) {

		if (resultCode == 1 || mCityNum != SLAppication.getMyArea().size()) {
			mCityNum = SLAppication.getMyArea().size();
			getAllWeatherData();
		}
	}

	public SunModel getCurSunModel() {
		if (mSunModels != null && mSunModels.size() != 0)
			return mSunModels.get(mCurPageIndex);
		else
			return null;
	}

	@Override
	public void onRefresh() {

		if (mSunModels.size() < 1) {
			getAllWeatherData();
		} else {
			onRefreshData();
		}
	}

	public void setOnRefresh() {
		setRefreshing(true);
		getAllWeatherData();
	}

	/**
	 * 让刷新进度环停止
	 * 
	 * @param refreshing
	 */
	private void setRefreshing(boolean refreshing) {
		mSwipeRefreshLayout.setRefreshing(refreshing);
		if (mRefreshItem == null)
			return;

		if (refreshing) {
			mRefreshItem.setEnabled(false);
			mRefreshItem.setActionView(R.layout.actionbar_refresh_progress);
		} else {
			mRefreshItem.setEnabled(true);
			mRefreshItem.setActionView(null);
		}
	}

	/**
	 * ViewPage Listener
	 */
	private class MyPageViewListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			try {
				// 设置当前城市，并更新Widget
				SLAppication.setCurCityIndex(mCurPageIndex = position);
				// LogUtil.i(TAG,
				// App.getMyArea().get(App.getCurCityIndex()).getCityName());
				getActivity().sendBroadcast(new Intent(WeatherUpdateService.ACTION_SWITH_CITY));

				// 更新ListView
				mFutureWeatherListView.startAnimation(fadeIn);
				mFutureWeatherListView.smoothScrollToPosition(0);
				mFutureWeatherAdapter.appendToList(mSunModels.get(position).weather);
				/*
				 * if (activity != null) {
				 * activity.updateHourly(mSunModels.get(mCurPageIndex
				 * ).hourlyForecast); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		RequestManager.cancelAll(this);
	}

}