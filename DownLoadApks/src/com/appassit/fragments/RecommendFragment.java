package com.appassit.fragments;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appassit.R;
import com.appassit.adapter.RecommendAppAdapter;
import com.appassit.common.Utils;
import com.appassit.enums.QuickReturnType;
import com.appassit.interfaces.QuickReturnInterface;
import com.appassit.listener.QuickReturnListViewOnScrollListener;
import com.appassit.model.ApkUrlParser;
import com.appassit.model.DownloadInfo;
import com.appassit.service.DownloadManager;
import com.appassit.service.DownloadService;
import com.appassit.tools.FileUtils;
import com.appassit.widget.jazzylistview.JazzyListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;

public class RecommendFragment extends Fragment {
	// region Member Variables
	QuickReturnInterface mCoordinator;
	View mPlaceHolderView;
	@ViewInject(R.id.recomend_list)
	JazzyListView mListView;
	@ViewInject(R.id.quick_return_footer_ll)
	LinearLayout mQuickReturnFooterLinearLayout;
	//
	DownloadManager downloadManager;
	ArrayList<DownloadInfo> infos;
	ApkUrlParser parser;
	FileUtils fileUtils;
	String target = null;
	RecommendAppAdapter adapter = null;

	// region Lifecycle Methods
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof QuickReturnInterface) {
			mCoordinator = (QuickReturnInterface) activity;
		} else
			throw new ClassCastException("Parent container must implement the QuickReturnInterface");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		downloadManager = DownloadService.getDownloadManager(getActivity());
		adapter = new RecommendAppAdapter();
		infos = new ArrayList<DownloadInfo>();
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_app_recommend, container, false);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// int headerHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
		// int indicatorHeight = QuickReturnUtils.dp2px(getActivity(), 5);
		// int headerTranslation = -headerHeight + QuickReturnUtils.getActionBarHeight(getActivity()) + indicatorHeight;
		// int footerTranslation = -headerHeight + QuickReturnUtils.getActionBarHeight(getActivity());

		int headerHeight = getResources().getDimensionPixelSize(R.dimen.twitter_header_height);
		int footerHeight = getResources().getDimensionPixelSize(R.dimen.twitter_footer_height);
		int indicatorHeight = Utils.dp2px(getActivity(), 4);
		int headerTranslation = -headerHeight + indicatorHeight;
		int footerTranslation = -footerHeight + indicatorHeight;

		QuickReturnListViewOnScrollListener scrollListener = new QuickReturnListViewOnScrollListener(QuickReturnType.TWITTER, mCoordinator.getTabs(),
				headerTranslation, mQuickReturnFooterLinearLayout, -footerTranslation);
		scrollListener.setCanSlideInIdleScrollState(true);
		mListView.setOnScrollListener(scrollListener);

		mPlaceHolderView = getActivity().getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
		mListView.addHeaderView(mPlaceHolderView);
		mListView.setAdapter(adapter);
	}

	public void onResume() {
		super.onResume();
	}

	private void initData() {
		// TODO Auto-generated method stub
		Log.e("YM", "initData");
		parser = new ApkUrlParser();
		try {
			infos = parser.parse(getActivity().getAssets().open("apkurl.xml"));
			Log.e("YM", "infos.size:" + infos.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.add(infos);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			if (adapter != null && downloadManager != null) {
				downloadManager.backupDownloadInfoList();
			}
		} catch (DbException e) {
		}
	}

	public void onDestroy() {
		super.onDestroy();
	}
}
