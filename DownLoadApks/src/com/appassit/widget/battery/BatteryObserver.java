package com.appassit.widget.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BatteryObserver implements IObserver {

	/**
	 * 是否在充电
	 */
	public static final String PARAM_ISCHARGING = "param_ischarging";
	/**
	 * 当前电量
	 */
	public static final String PARAM_LEVEL = "param_level";
	/**
	 * 总共电量
	 */
	public static final String PARAM_SCALE = "param_scale";

	private static BatteryObserver instance;

	public synchronized static BatteryObserver getInstance(Context context) {
		if (instance == null) {
			instance = new BatteryObserver(context);
		}
		return instance;
	}

	private Context context;
	private boolean isRegister = false;
	private int status = -1;
	private int level = -1;
	private int scale = -1;
	private OnBatteryChange onBatteryChange;

	public BatteryObserver(Context context) {
		this.context = context;
	}

	public void register() {
		if (!isRegister) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_BATTERY_CHANGED);
			context.registerReceiver(mBroadcastReceiver, filter);
			isRegister = true;
		}
	}

	public void unRegister() {
		if (isRegister) {
			context.unregisterReceiver(mBroadcastReceiver);
			isRegister = false;
		}
	}

	public boolean isRegister() {
		return isRegister;
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				int status = intent.getIntExtra("status", 0);
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 0);
				BatteryObserver.this.status = status;
				BatteryObserver.this.level = level;
				BatteryObserver.this.scale = scale;
				if (onBatteryChange != null) {
					onBatteryChange.onChange(status, level, scale);
				}
			}
		}
	};

	public int getStatus() {
		return status;
	}

	public int getLevel() {
		return level;
	}

	public int getScale() {
		return scale;
	}

	public OnBatteryChange getOnBatteryChange() {
		return onBatteryChange;
	}

	public void setOnBatteryChange(OnBatteryChange onBatteryChange) {
		this.onBatteryChange = onBatteryChange;
	}

	public interface OnBatteryChange {
		public void onChange(int status, int level, int scale);
	}
}