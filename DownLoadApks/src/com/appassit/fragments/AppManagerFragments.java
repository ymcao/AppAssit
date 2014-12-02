package com.appassit.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appassit.R;
import com.appassit.activitys.SLAppication;
import com.appassit.common.Utils;
import com.appassit.enums.QuickReturnType;
import com.appassit.interfaces.QuickReturnInterface;
import com.appassit.listener.QuickReturnListViewOnScrollListener;
import com.appassit.model.AppInfo;
import com.appassit.provider.AppInfoProvider;
import com.appassit.widget.RippleView;
import com.appassit.widget.googleprogress.GoogleProgressBar;
import com.appassit.widget.jazzylistview.JazzyListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AppManagerFragments extends Fragment {
	// region Member Variables
	@ViewInject(R.id.tv_avail_rom)
	TextView tv_avail_rom;

	@ViewInject(R.id.tv_avail_sd)
	TextView tv_avail_sd;

	@ViewInject(android.R.id.list)
	JazzyListView lv_app_manager;

	@ViewInject(R.id.ll_loading)
	GoogleProgressBar ll_loading;
	/**
	 * 当前程序信息的状态。
	 */
	@ViewInject(R.id.tv_status)
	TextView tv_status;
	@ViewInject(R.id.quick_return_footer_ll)
	LinearLayout mQuickReturnFooterLinearLayout;

	List<AppInfo> appInfos;
	/**
	 * 用户应用程序的集合
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * 系统应用程序的集合
	 */
	// private List<AppInfo> systemAppInfos;
	/**
	 * 所有的应用程序包信息
	 */
	private QuickReturnInterface mCoordinator;
	private View mPlaceHolderView;
	/**
	 * 被点击的条目。
	 */
	private AppInfo appInfo;

	private AppManagerAdapter adapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof QuickReturnInterface) {
			mCoordinator = (QuickReturnInterface) activity;
		} else
			throw new ClassCastException("Parent container must implement the QuickReturnInterface");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_app_manager, container, false);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
		tv_avail_sd.setText("SD卡可用空间" + "：" + Formatter.formatFileSize(getActivity(), sdsize));
		tv_avail_rom.setText("内存可用空间：" + Formatter.formatFileSize(getActivity(), romsize));
		int headerHeight = getResources().getDimensionPixelSize(R.dimen.twitter_header_height);
		int footerHeight = getResources().getDimensionPixelSize(R.dimen.twitter_footer_height);
		int indicatorHeight = Utils.dp2px(getActivity(), 4);
		int headerTranslation = -headerHeight + indicatorHeight;
		int footerTranslation = -footerHeight + indicatorHeight;
		QuickReturnListViewOnScrollListener scrollListener = new QuickReturnListViewOnScrollListener(QuickReturnType.TWITTER, mCoordinator.getTabs(),
				headerTranslation, mQuickReturnFooterLinearLayout, -footerTranslation);
		scrollListener.setCanSlideInIdleScrollState(true);
		lv_app_manager.setOnScrollListener(scrollListener);
		mPlaceHolderView = getActivity().getLayoutInflater().inflate(R.layout.view_header_placeholder, lv_app_manager, false);
		lv_app_manager.addHeaderView(mPlaceHolderView);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillData();
	}

	@Override
	public void onDestroyView() {
		Log.e("YM", "onDestroyView");
		super.onDestroyView();
		lv_app_manager.setAdapter(null);
	}

	void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				userAppInfos = new ArrayList<AppInfo>();
				appInfos = AppInfoProvider.getAppInfos(SLAppication.getContext());
				// systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appInfos) {
					if (info.isUserApp()) {
						userAppInfos.add(info);
					} else {
						// systemAppInfos.add(info);
					}
				}
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (adapter == null) {
							adapter = new AppManagerAdapter();
						}
						// tv_status.setText("用户程序：" + userAppInfos.size() + "个");
						lv_app_manager.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						ll_loading.setVisibility(View.GONE);
					}
				});
			}
		}).start();
	}

	private class AppManagerAdapter extends BaseAdapter {

		// 控制listview有多少个条目

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			final ViewHolder holder;
			appInfo = userAppInfos.get(position);
			// appInfo = systemAppInfos.get(newposition);
			// }
			if (convertView != null && convertView instanceof RelativeLayout) {
				// 不仅需要检查是否为空，还要判断是否是合适的类型去复用
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(SLAppication.getContext(), R.layout.layout_manager_appinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.riple_uninstall = (RippleView) view.findViewById(R.id.riple_uninstall);
				view.setTag(holder);
			}
			holder.position = position;
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("手机内存");
			} else {
				holder.tv_location.setText("外部存储");
			}
			holder.riple_uninstall.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					appInfo = userAppInfos.get(holder.position);
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					intent.setAction("android.intent.action.DELETE");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:" + appInfo.getPackname()));
					startActivityForResult(intent, 0);
				}
			});
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userAppInfos.size();
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
		RippleView riple_uninstall;
		int position = 0;
	}

	/**
	 * 获取某个目录的可用空间
	 * 
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {
		StatFs statf = new StatFs(path);
		statf.getBlockCount();// 获取分区的个数
		long size = statf.getBlockSize();// 获取分区的大小
		long count = statf.getAvailableBlocks();// 获取可用的区块的个数
		return size * count;
	}

}
