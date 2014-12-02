package com.appassit.activitys;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appassit.R;
import com.appassit.common.Utils;
import com.appassit.fragments.AppManagerFragments;
import com.appassit.fragments.BeautyFragment;
import com.appassit.fragments.RecommendFragment;
import com.appassit.fragments.ToadyFragment_;
import com.appassit.interfaces.QuickReturnInterface;
import com.appassit.tools.NetUtil;
import com.appassit.tools.ToastUtil;
import com.appassit.widget.ActionBarDrawerToggle;
import com.appassit.widget.ArcProgress;
import com.appassit.widget.DrawerArrowDrawable;
import com.appassit.widget.PagerSlidingTabStrip;
import com.appassit.widget.RippleView;
import com.appassit.widget.battery.BatteryChangeView;
import com.appassit.widget.battery.BatteryObserver;
import com.appassit.widget.battery.BatteryObserver.OnBatteryChange;
import com.appassit.widget.pieView.PieControl;
import com.appassit.widget.pieView.PieViewControllee;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.nineoldandroids.view.ViewHelper;

@EActivity(R.layout.activity_main_app)
public class MainAppActivity extends BaseActivity implements QuickReturnInterface, SLAppication.EventHandler, OnBatteryChange, PieViewControllee {

	// region Member Variables
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private LinearLayout mTabsLinearLayout;
	BatteryObserver batteryObserver;
	@ViewById(R.id.contentFrame)
	FrameLayout contentFrame;
	@ViewById(R.id.memory_progress)
	ArcProgress memory_progress;
	@ViewById(R.id.exitRipView)
	RippleView exitRipView;
	@ViewById(R.id.root_BatteryView)
	BatteryChangeView rootBattryView;
	@ViewById(R.id.locationBtn)
	ImageView locationBtn;
	@ViewById(R.id.locationText)
	TextView locationText;
	@ViewById(R.id.tabs)
	PagerSlidingTabStrip mTabs;
	@ViewById(R.id.pager)
	ViewPager mViewPager;
	// @ViewInject(R.id.navigationDrawerListViewWrapper)
	// NavigationDrawerView mNavigationDrawerListViewWrapper;
	@ViewById(R.id.linearDrawer)
	LinearLayout mLinearDrawerLayout;
	@ViewById(R.id.drawerLayout)
	DrawerLayout mDrawerLayout;
	// @ViewInject(R.id.leftDrawerListView)
	// ListView leftDrawerListView;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerArrowDrawable drawerArrow;
	// public ToadyFragment todayFragment;
	// private List<NavigationDrawerItem> navigationItems;
	// private ActionBar actionBar;
	private LocationClient mLocationClient;
	private Application mApplication;
	private static final int LOACTION_OK = 0;
	private static final int UPDATE_MEMORY_PERCENT = 2;
	@ViewById(R.id.lv_weather)
	ListView mWeatherListView;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOACTION_OK:
				String cityName = (String) msg.obj;
				locationText.setText("当前位置 ：" + cityName);
				break;
			case UPDATE_MEMORY_PERCENT:
				int progress = (int) (Utils.getAvailMemory() * 100 / Utils.getTotalMemory());
				memory_progress.setProgress(100 - progress);
				break;
			default:
				break;
			}
		}

	};

	private ViewPager.OnPageChangeListener mTabsOnPageChangeListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			ViewHelper.setTranslationY(mTabs, 0);
		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
				TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);
				if (i == position) {
					tv.setTextColor(getResources().getColor(R.color.cornflower_blue));
					tv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
				} else {
					tv.setTextColor(getResources().getColor(R.color.cornflower_blue));
					tv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	@AfterViews
	void initActivity() {
		initView();
		if (batteryObserver != null) {
			batteryObserver.register();
			batteryObserver.setOnBatteryChange(this);
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		setTitle(R.string.app_name);
		PieControl mPieControl = new PieControl(MainAppActivity.this, this);
		mPieControl.attachToContainer(contentFrame);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mTabs.setAllCaps(false);
		mTabs.setShouldExpand(true);
		mTabs.setTextSize(Utils.dp2px(this, 18));
		// mTabs.setUnderlineColor(getResources().getColor(R.color.gray5));
		mTabs.setUnderlineHeight(Utils.dp2px(this, 1));
		mTabs.setTabBackground(R.drawable.selector_bg_tab);
		mTabs.setDividerColor(getResources().getColor(android.R.color.transparent));
		mTabs.setIndicatorColorResource(R.color.cornflower_blue);
		mTabs.setIndicatorHeight(Utils.dp2px(this, 4));
		mTabs.setOnPageChangeListener(mTabsOnPageChangeListener);
		mTabs.setViewPager(mViewPager);
		// Set first tab selected
		mTabsLinearLayout = ((LinearLayout) mTabs.getChildAt(0));
		for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
			TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);

			if (i == 0) {
				tv.setTextColor(getResources().getColor(R.color.cornflower_blue));
				tv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
			} else {
				tv.setTextColor(getResources().getColor(R.color.cornflower_blue));
				tv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
			}
		}
		/*
		 * navigationItems = new ArrayList<NavigationDrawerItem>();
		 * navigationItems.add(new
		 * NavigationDrawerItem(getString(R.string.about), true));
		 * navigationItems.add(new
		 * NavigationDrawerItem(getString(R.string.settings), true));
		 * mNavigationDrawerListViewWrapper.replaceWith(navigationItems);
		 */
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		SLAppication.mListeners.add(this);
		mApplication = SLAppication.getInstance();
		batteryObserver = BatteryObserver.getInstance(this);
		mLocationClient = ((SLAppication) mApplication).getLocationClient();
		mLocationClient.registerLocationListener(mLocationListener);
		// if (TextUtils.isEmpty(mSpUtil.getCity())) {
		if (NetUtil.isNetworkConnected()) {
			mLocationClient.start();
			mLocationClient.requestLocation();
			locationText.setText("正在定位...");
		} else {
			Toast.makeText(SLAppication.getContext(), R.string.net_err, Toast.LENGTH_SHORT).show();
		}
	}

	@Click(R.id.locationBtn)
	void locationRefresh() {
		if (NetUtil.isNetworkConnected()) {
			if (!mLocationClient.isStarted())
				mLocationClient.start();
			mLocationClient.requestLocation();
			locationText.setText("正在定位...");
		} else {
			locationText.setText(R.string.net_err);
		}
	}

	@Click(R.id.memory_progress)
	void clearMemory() {
		final long buforeMemory = Utils.getAvailMemory() / (1024 * 1024);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				Utils.clearBackMemory();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String str = "清理" + (Utils.getAvailMemory() / (1024 * 1024) - buforeMemory) + "MB内存";
						ToastUtil.showShort(str);
						int finalprogress = (int) (Utils.getAvailMemory() * 100 / Utils.getTotalMemory());
						memory_progress.setProgress(100 - finalprogress);
					}
				});
				Looper.loop();
				//
			}
		}).start();
	}

	@Click(R.id.exitRipView)
	void exitApp() {
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		rootBattryView.onResume();
		mHandler.sendEmptyMessage(UPDATE_MEMORY_PERCENT);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		rootBattryView.onPause();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// region QuickReturnInterface Methods
	@Override
	public PagerSlidingTabStrip getTabs() {
		return mTabs;
	}

	/**
	 * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new BeautyFragment();
			case 1:
				return new ToadyFragment_();
			case 2:
				return new RecommendFragment();
			case 3:
				return new AppManagerFragments();
			default:
				return new RecommendFragment();

			}
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.beauty);
			case 1:
				return getString(R.string.discover);
			case 2:
				return getString(R.string.home);
			case 3:
				return getString(R.string.activity);
			}
			return null;
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (batteryObserver != null) {
			batteryObserver.unRegister();
		}
	}

	BDLocationListener mLocationListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// do nothing
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			// mActionBar.setProgressBarVisibility(View.GONE);
			// mUpdateBtn.setVisibility(View.VISIBLE);
			// mUpdateProgressBar.setVisibility(View.GONE);
			if (location == null || TextUtils.isEmpty(location.getCity())) {
				return;
			}
			// String cityName = location.getCity();
			String districtName = location.getDistrict();
			mLocationClient.stop();
			Message msg = mHandler.obtainMessage();
			msg.what = LOACTION_OK;
			msg.obj = districtName;
			mHandler.sendMessage(msg);
		}
	};

	/*
	 * @OnItemClick(R.id.leftDrawerListView) public void OnItemClick(int
	 * position, long id) { Log.e("YM", "position:" + position + "id:" + id); if
	 * (mDrawerLayout.isDrawerOpen(mLinearDrawerLayout)) {
	 * mDrawerLayout.closeDrawer(mLinearDrawerLayout);
	 * onNavigationDrawerItemSelected(position); selectItem(position); } }
	 */
	/*
	 * private void selectItem(int position) {
	 * 
	 * if (leftDrawerListView != null) {
	 * leftDrawerListView.setItemChecked(position, true); //
	 * navigationItems.get(position).setSelected(true);
	 * actionBar.setTitle(navigationItems.get(position).getItemName()); }
	 * 
	 * if (mLinearDrawerLayout != null) {
	 * mDrawerLayout.closeDrawer(mLinearDrawerLayout); } }
	 * 
	 * private void onNavigationDrawerItemSelected(int position) { switch
	 * (position) { case 0: break; case 1: break; }
	 * 
	 * }
	 */

	@Override
	public void onCityComplite() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNetChange() {
		// TODO Auto-generated method stub
		if (!NetUtil.isNetworkConnected())
			Toast.makeText(SLAppication.getContext(), R.string.net_err, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onChange(int status, int level, int scale) {
		// TODO Auto-generated method stub
		rootBattryView.onBattery(status, level, scale);
		rootBattryView.onUpdate();
	}
}