package com.appassit.activitys;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.animation.LayoutTransition;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appassit.R;
import com.appassit.adapter.CityAdapter;
import com.appassit.model.city.AreaModel;
import com.appassit.model.city.BaseCityModel;
import com.appassit.model.city.CityModel;
import com.appassit.model.city.ProvicneModel;
import com.appassit.tools.ToastUtil;
import com.appassit.widget.jazzylistview.JazzyHelper;
import com.appassit.widget.jazzylistview.JazzyListView;
import com.appassit.widget.swipeback.SwipeBackActivity;

@EActivity(R.layout.activity_weather_city)
public class CityActivity extends SwipeBackActivity {

	protected final static String TAG = "CityActivity";

	/** 动画执行时间 */
	protected final static int ANIM_DURATON = 5 * 100;

	/** 城市 */
	@ViewById(R.id.ll_city_content)
	LinearLayout mLLCityContent;

	/** 当前选择城市 */
	@ViewById(R.id.rl_city)
	RelativeLayout mllSelectCity;

	/** 当前选择省市县名称 */
	@ViewById(R.id.tv_area1)
	TextView tvArea1;
	@ViewById(R.id.tv_area2)
	TextView tvArea2;
	@ViewById(R.id.tv_area3)
	TextView tvArea3;

	/** 当前选中的城市信息 */
	List<CityModel> mCurCityModels;
	List<AreaModel> mCurAreaModels;
	AreaModel mCurAreaModel;

	/** 城市列表-省 */
	@ViewById(R.id.lv_area1)
	JazzyListView mLvProvicne;

	/** 城市列表-城市 */
	@ViewById(R.id.lv_area2)
	JazzyListView mLvCity;

	/** 城市列表-区/县 */
	@ViewById(R.id.lv_area3)
	JazzyListView mLvTown;

	/** 城市适配器 */
	@Bean
	CityAdapter mAdapterProvicne;
	@Bean
	CityAdapter mAdapterCity;
	@Bean
	CityAdapter mAdapterTown;

	/** 内容动画 */
	private LayoutTransition mTransition;

	@AfterViews
	void initActivity() {
		addContentAnim();
		initView();
		getProvicneList();
	}

	private void initView() {

		mLvProvicne.setTransitionEffect(JazzyHelper.SLIDE_IN);
		mLvCity.setTransitionEffect(JazzyHelper.SLIDE_IN);
		mLvTown.setTransitionEffect(JazzyHelper.SLIDE_IN);

		mLvProvicne.setAdapter(mAdapterProvicne);
		mLvCity.setAdapter(mAdapterCity);
		mLvTown.setAdapter(mAdapterTown);
	}

	@Background
	void getProvicneList() {
		List<BaseCityModel> baseCityModels = new ArrayList<BaseCityModel>();
		baseCityModels.addAll(SLAppication.getProvicneModels());
		updateList(1, baseCityModels);
	}

	@Background
	void getCityList(String id) {
		List<BaseCityModel> baseCityModels = new ArrayList<BaseCityModel>();
		baseCityModels.addAll(getCurCityList(id));
		updateList(2, baseCityModels);
	}

	@Background
	void getAreaList(String id) {
		List<BaseCityModel> baseCityModels = new ArrayList<BaseCityModel>();
		baseCityModels.addAll(getCurAreaList(id));
		updateList(3, baseCityModels);
	}

	@UiThread
	void updateList(int type, List<BaseCityModel> models) {
		switch (type) {
		case 1:
			mAdapterProvicne.appendToList(models);
			break;
		case 2:
			mAdapterCity.appendToList(models);
			break;
		case 3:
			mAdapterTown.appendToList(models);
			break;
		}
	}

	/**
	 * 获取当前省-城市列表信息
	 * 
	 * @param id
	 */
	private List<CityModel> getCurCityList(String id) {
		for (ProvicneModel model : SLAppication.getProvicneModels()) {
			if (model.getCityId().equals(id)) {
				mCurCityModels = model.getCityModels();
			}
		}
		return mCurCityModels;
	}

	/**
	 * 获取当前城市-地区列表信息
	 * 
	 * @param id
	 */
	private List<AreaModel> getCurAreaList(String id) {
		for (CityModel model : mCurCityModels) {
			if (model.getCityId().equals(id)) {
				mCurAreaModels = model.getAreaModels();
			}
		}
		return mCurAreaModels;
	}

	/**
	 * 获取当前地区信息
	 * 
	 * @param id
	 * @return
	 */
	private void setCurArea(String id) {
		for (AreaModel model : mCurAreaModels) {
			if (model.getCityId().equals(id)) {
				mCurAreaModel = model;
			}
		}
	}

	/**
	 * 添加内容动画
	 */
	private void addContentAnim() {
		mTransition = new LayoutTransition();
		mTransition.setDuration(ANIM_DURATON);
		mTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
			@Override
			public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
			}

			@Override
			public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
			}
		});
		mLLCityContent.setLayoutTransition(mTransition);
	}

	@ItemClick(R.id.lv_area1)
	void provicneItemClick(BaseCityModel model) {

		// 清除数据
		mAdapterTown.clear();

		// 获取数据
		tvArea1.setText(model.getCityName());
		getCityList(model.getCityId());

		mllSelectCity.setVisibility(View.VISIBLE);
		tvArea1.setVisibility(View.VISIBLE);
		tvArea2.setVisibility(View.INVISIBLE);
		tvArea3.setVisibility(View.INVISIBLE);
		mLvCity.setVisibility(View.VISIBLE);
		mLvTown.setVisibility(View.GONE);
	}

	@ItemClick(R.id.lv_area2)
	void cityItemClick(BaseCityModel model) {

		// 获取数据
		tvArea2.setText(model.getCityName());
		getAreaList(model.getCityId());

		tvArea2.setVisibility(View.VISIBLE);
		tvArea3.setVisibility(View.INVISIBLE);
		mLvTown.setVisibility(View.VISIBLE);
	}

	@ItemClick(R.id.lv_area3)
	void areaItemClick(BaseCityModel model) {

		tvArea3.setVisibility(View.VISIBLE);
		tvArea3.setText(model.getCityName());

		setCurArea(model.getCityId());
		addCity(SLAppication.addMyArea(mCurAreaModel));
	}

	private void addCity(String msg) {
		if (msg == null) {

		} else if (msg.equals(getString(R.string.city_add_success))) {
			setResult(1);
			finish();
		} else {
			ToastUtil.showShort(msg);
		}
	}
}
