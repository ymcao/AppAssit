package com.appassit.activitys;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {

	private static final ArrayList<Activity> sActivityArray = new ArrayList<Activity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		sActivityArray.add(this);

	}

	public void setTitle(String title) {
		getSupportActionBar().setTitle(title);
	}

	public void setTitle(int resId) {
		getSupportActionBar().setTitle(resId);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (isFinishing()) {
			sActivityArray.remove(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private static void exitMemoryActivities() {
		Activity activity = null;
		while (!sActivityArray.isEmpty()) {
			activity = sActivityArray.remove(0);
			if (activity != null) {
				activity.finish();
			}
		}
	}

	public void onExitApp() {
		exitMemoryActivities();
	}
}
