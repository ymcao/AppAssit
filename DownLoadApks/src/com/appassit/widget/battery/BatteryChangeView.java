package com.appassit.widget.battery;

import android.content.Context;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appassit.R;

public class BatteryChangeView extends FrameLayout implements ILife {

	TextView tvBattery;
	private int status = -1;
	private int level = -1;
	private int scale = -1;

	public BatteryChangeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		findViews();
		setValues();
	}

	private void findViews() {
		tvBattery = (TextView) findViewById(R.id.tv_battery);
	}

	private void setValues() {

		reSetValues();
	}

	private void reSetValues() {
		onBattery(status, level, scale);
	}

	public void onResume() {
		reSetValues();
	}

	public void onUpdate() {
		reSetValues();
	}

	public void onBattery(int status, int level, int scale) {
		this.status = status;
		this.level = level;
		this.scale = scale;
		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
			tvBattery.setText("正在充电:" + getBatteryPrecent(level, scale) + "%");
		} else {
			tvBattery.setText("剩余电量:" + getBatteryPrecent(level, scale) + "%");
		}
		if (getBatteryPrecent(level, scale) <= 20) {
			tvBattery.setTextColor(getResources().getColor(R.color.cpb_red));
		}
	}

	private int getBatteryPrecent(int level, int scale) {
		int b = (int) (level * 100.0 / scale);
		return b;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

}