package com.appassit.activitys;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

import com.appassit.R;
import com.appassit.common.CitySaxParseHandler;
import com.appassit.common.Const;
import com.appassit.data.RequestManager;
import com.appassit.model.SunModel;
import com.appassit.model.city.AreaModel;
import com.appassit.model.city.ProvicneModel;
import com.appassit.tools.FileUtils;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class SLAppication extends Application {
	private static Context sContext;
	public static final boolean DEBUG = true;
	private LocationClient mLocationClient = null;
	public static int mNetWorkState;
	public static ArrayList<EventHandler> mListeners = new ArrayList<EventHandler>();
	private static Application mApplication;

	/** 城市列表 */
	private static List<ProvicneModel> mProvicneModels;
	private static List<AreaModel> mAreaModels;
	public static SunModel mCurSunModel;
	private static int mCurWeatherIndex;
	private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 50;
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	private static int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so quality is ignored but must be provided

	public static Context getContext() {
		return sContext;
	}

	public static synchronized Application getInstance() {
		return mApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initData();
		RequestManager.init(this);
	}

	private LocationClientOption getLocationClientOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");
		option.setServiceName(this.getPackageName());
		option.setScanSpan(0);
		option.disableCache(true);
		return option;
	}

	private void initData() {
		mApplication = this;
		sContext = this.getApplicationContext();
		mLocationClient = new LocationClient(this, getLocationClientOption());
		mAreaModels = new ArrayList<AreaModel>();
		mCurWeatherIndex = 0;
		initMyArea();
		initProvicneModels();
	}

	public synchronized LocationClient getLocationClient() {
		if (mLocationClient == null)
			mLocationClient = new LocationClient(this, getLocationClientOption());
		return mLocationClient;
	}

	/**
	 * 初始化城市列表
	 */
	private void initProvicneModels() {
		try {
			InputStream in = getAssets().open(Const.FILE_CITY_NAME);
			mProvicneModels = CitySaxParseHandler.getProvicneModel(in);
			// LogUtil.i(TAG, mProvicneModels.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<ProvicneModel> getProvicneModels() {
		return mProvicneModels;
	}

	/**
	 * 初始化我的城市
	 */
	private void initMyArea() {
		try {
			List<AreaModel> models = (List<AreaModel>) FileUtils.readObjsFromFile(Const.FILE_MY_AREA);
			if (models != null) {
				mAreaModels.addAll(models);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setCurCityIndex(int index) {
		mCurWeatherIndex = index;
	}

	public static int getCurCityIndex() {
		return mCurWeatherIndex;
	}

	/**
	 * 添加我的城市
	 * 
	 * @param model
	 * @return
	 */
	public static String addMyArea(AreaModel model) {
		if (model == null) {
			return null;
		}

		if (mAreaModels.size() >= 5) {
			return getContext().getString(R.string.city_exceed_num);
		} else {
			for (AreaModel areaModel : mAreaModels) {
				if (areaModel.getCityId().equals(model.getCityId())) {
					return getContext().getString(R.string.city_already_exists);
				}
			}
			// 添加到第一位
			mAreaModels.add(0, model);
			// 重新保存文件
			FileUtils.writeObjsToFile(mAreaModels, Const.FILE_MY_AREA, Context.MODE_PRIVATE);
			// 返回添加城市结果信息
			return getContext().getString(R.string.city_add_success);
		}
	}

	/**
	 * 删除城市信息
	 * 
	 * @param position
	 * @return
	 */
	public static AreaModel removeMyArea(int position) {
		// 删除数据
		AreaModel model = mAreaModels.remove(position);
		// 重新保存文件
		FileUtils.writeObjsToFile(mAreaModels, Const.FILE_MY_AREA, Context.MODE_PRIVATE);
		return model;
	}

	public static boolean removeMyArea(AreaModel areaModel) {
		// 删除数据
		boolean is = mAreaModels.remove(areaModel);
		// 重新保存文件
		FileUtils.writeObjsToFile(mAreaModels, Const.FILE_MY_AREA, Context.MODE_PRIVATE);
		return is;
	}

	public static List<AreaModel> getMyArea() {
		return mAreaModels;
	}

	public static abstract interface EventHandler {
		public abstract void onCityComplite();

		public abstract void onNetChange();
	}
}
