package com.appassit.tools;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.appassit.activitys.SLAppication;

public class AndroidOSUtils {
	// private static final String TAG = "AndroidOSUtils";

	private static int sOsWidth = -1;

	private static int sOsHeight = -1;

	private static DisplayMetrics sDisplayMetrics;

	private static DisplayMetrics getDisplayMetrics() {
		if (sDisplayMetrics == null) {
			synchronized (AndroidOSUtils.class) {
				if (sDisplayMetrics == null) {
					sDisplayMetrics = SLAppication.getContext().getResources().getDisplayMetrics();
				}
			}
		}
		return sDisplayMetrics;
	}

	private static void initOsHeigth() {
		if (sOsHeight <= 0) {
			synchronized (AndroidOSUtils.class) {
				if (sOsHeight <= 0) {
					sOsHeight = getDisplayMetrics().heightPixels;
				}
			}
		}
	}

	private static void initOSWidth() {
		if (sOsWidth <= 0) {
			synchronized (AndroidOSUtils.class) {
				if (sOsWidth <= 0) {
					sOsWidth = getDisplayMetrics().widthPixels;
				}
			}
		}
	}

	public static int getDisplayHeight() {
		initOsHeigth();
		return sOsHeight;
	}

	public static int getDisplayWidth() {
		initOSWidth();
		return sOsWidth;
	}

	public static int dip2Pix(float dip) {
		return ((int) (dip * getDisplayMetrics().density + 0.5f));
	}

	public static int calculateByRatio(int width, float radio) {
		return (int) (width * radio);
	}

	public static void clear() {
		sDisplayMetrics = null;
	}

	public static String getMEID() {
		return ((TelephonyManager) SLAppication.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	public static int getDispalyDensityDpi() {
		return getDisplayMetrics().densityDpi;
	}

	public static String getString(int resId, Object... args) {
		try {
			return SLAppication.getContext().getString(resId, args);
		} catch (NotFoundException e) {
		}
		return null;
	}

	public static int getDimens(int resId) {
		try {
			return (int) SLAppication.getContext().getResources().getDimension(resId);
		} catch (NotFoundException e) {
		}
		return 0;
	}
}
