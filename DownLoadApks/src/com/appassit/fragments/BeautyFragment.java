package com.appassit.fragments;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appassit.R;
import com.appassit.adapter.BeautyAdapter;
import com.appassit.data.GsonBeautyRequest;
import com.appassit.data.RequestManager;
import com.appassit.http.SLApis;
import com.appassit.listener.OnFilterDoubleClickListener;
import com.appassit.model.Beauty;
import com.appassit.tools.NetUtil;
import com.appassit.widget.googleprogress.GoogleProgressBar;
import com.appassit.widget.xlistview.XListView;
import com.appassit.widget.xlistview.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class BeautyFragment extends Fragment implements IXListViewListener {
	@ViewInject(R.id.listview)
	private XListView mListView;
	@ViewInject(R.id.ll_loading)
	private GoogleProgressBar progressBar;
	@ViewInject(R.id.img_nodata)
	ImageView img_nodata;
	private BeautyAdapter mAdapter;
	private int mPageNo = 1;
	private Map<String, String> mMaps;
	private boolean hasMore = false;
	private boolean beginLoading = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("YM", "Beauty-onCreate");
		mMaps = new HashMap<String, String>();
		mAdapter = new BeautyAdapter(new OnFilterDoubleClickListener() {

			@Override
			public void onClicked(View v) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_app_beauty, container, false);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.e("YM", "Beauty-onViewCreated");
		getBeautyData();
		mListView.setXListViewListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		mListView.setDividerHeight((int) getResources().getDimension(R.dimen.album_past_space));
		int padding = (int) getResources().getDimension(R.dimen.album_margin);
		mListView.setPadding(padding, 0, padding, 0);
	}

	public void onResume() {
		super.onResume();
		Log.e("YM", "Beauty-onResume");
	}

	/*private void requestData() {
		// TODO Auto-generated method stub
		// SLApis.getRecommend(mPageNo, mApiListener);
		getBeautyData();
	}*/

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mPageNo = 1;
		getBeautyData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		Log.e("YM", "onLoadMore()");
		// /if(hasMore)
		getBeautyData();
	}

	@OnClick(R.id.img_nodata)
	void retry(View view) {
		if (NetUtil.isNetworkConnected()) {
			getBeautyData();
		}
	}

	private void getBeautyData() {
		if (beginLoading)
			progressBar.setVisibility(View.VISIBLE);
		img_nodata.setVisibility(View.GONE);
		mMaps.put("page", String.valueOf(mPageNo));
		executeRequest(new GsonBeautyRequest<Beauty>(SLApis.GET_RECOMMEND_LIST, Beauty.class, mMaps, responseListener(), errorListener()));
	}

	protected void executeRequest(Request<?> request) {
		RequestManager.addRequest(request, this);
	}

	private Response.Listener<Beauty> responseListener() {
		return new Response.Listener<Beauty>() {
			@Override
			public void onResponse(Beauty beauty) {
				if (beauty != null) {

					if (beginLoading)
						progressBar.setVisibility(View.GONE);
					beginLoading = false;
					/**/
					img_nodata.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					if (beauty.response != null && !TextUtils.isEmpty(beauty.response.message)) {
						// ToastUtils.showShortToast(rcm.response.message);
					}
					if (mPageNo == 1) {
						mAdapter.set(beauty.list);
						mListView.stopRefresh();
						mListView.setRefreshTime(new Date().toLocaleString());
					} else {
						mAdapter.add(beauty.list);
						mListView.stopLoadMore();
					}
					if (beauty.pageinfo != null && beauty.pageinfo.hasNext()) {
						hasMore = true;
					} else {
						hasMore = false;
					}
					// Log.e("YM", "hasMore:"+hasMore);
					if (hasMore) {
						mPageNo++;
					}
					mListView.setHasMoreData(hasMore);
					return;
				}
				/*if (mPageNo == 1) {
					mListView.stopRefresh();
					mListView.setRefreshTime(new Date().toLocaleString());
				} else {
					mListView.stopLoadMore();
				}*/
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
				Log.e("YM", "onErrorResponse");
				progressBar.setVisibility(View.GONE);
				mListView.setVisibility(View.GONE);
				img_nodata.setVisibility(View.VISIBLE);
			}
		};
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.e("YM", "Beauty-onDestroyView()");
		mListView.setAdapter(null);
		RequestManager.cancelAll(this);
	}
}
