package com.appassit.listener;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnFilterDoubleClickListener implements OnClickListener {
	private static int MIN_WAIT_TIME = 500;
	private long mLastClickedTime = 0;

	@Override
	public final void onClick(View v) {
		long now = System.currentTimeMillis();
		if (now - mLastClickedTime < MIN_WAIT_TIME) {
			return;
		}
		mLastClickedTime = now;
		onClicked(v);
	}

	public abstract void onClicked(View v);
}
