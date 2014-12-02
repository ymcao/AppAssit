package com.appassit.widget.navigationdrawer;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.appassit.R;
import com.appassit.model.NavigationDrawerItem;
import com.appassit.widget.misc.BetterViewAnimator;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Michal Bialas on 19/07/14.
 */
public class NavigationDrawerView extends BetterViewAnimator {

	@ViewInject(R.id.leftDrawerListView)
	ListView leftDrawerListView;

	private final NavigationDrawerAdapter adapter;

	public NavigationDrawerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		adapter = new NavigationDrawerAdapter(context);
	}

	public void replaceWith(List<NavigationDrawerItem> items) {
		adapter.replaceWith(items);
		setDisplayedChildId(R.id.leftDrawerListView);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ViewUtils.inject(this);
		leftDrawerListView.setAdapter(adapter);
	}

	public NavigationDrawerAdapter getAdapter() {
		return adapter;
	}
}
