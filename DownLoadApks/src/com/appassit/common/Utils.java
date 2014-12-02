/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appassit.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;

import com.appassit.activitys.SLAppication;

public class Utils {

	public static boolean sDebug;
	public static String sLogTag;

	private static final String TAG = "Utils";

	// UTF-8 encoding
	private static final String ENCODING_UTF8 = "UTF-8";

	private static WeakReference<Calendar> calendar;
	private static Dictionary<Integer, Integer> sListViewItemHeights = new Hashtable<Integer, Integer>();

	/**
	 * <p>
	 * Get UTF8 bytes from a string
	 * </p>
	 * 
	 * @param string
	 *            String
	 * @return UTF8 byte array, or null if failed to get UTF8 byte array
	 */
	public static byte[] getUTF8Bytes(String string) {
		if (string == null)
			return new byte[0];

		try {
			return string.getBytes(ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			/*
			 * If system doesn't support UTF-8, use another way
			 */
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				dos.writeUTF(string);
				byte[] jdata = bos.toByteArray();
				bos.close();
				dos.close();
				byte[] buff = new byte[jdata.length - 2];
				System.arraycopy(jdata, 2, buff, 0, buff.length);
				return buff;
			} catch (IOException ex) {
				return new byte[0];
			}
		}
	}

	/**
	 * <p>
	 * Get string in UTF-8 encoding
	 * </p>
	 * 
	 * @param b
	 *            byte array
	 * @return string in utf-8 encoding, or empty if the byte array is not encoded with UTF-8
	 */
	public static String getUTF8String(byte[] b) {
		if (b == null)
			return "";
		return getUTF8String(b, 0, b.length);
	}

	/**
	 * <p>
	 * Get string in UTF-8 encoding
	 * </p>
	 */
	public static String getUTF8String(byte[] b, int start, int length) {
		if (b == null) {
			return "";
		} else {
			try {
				return new String(b, start, length, ENCODING_UTF8);
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}
	}

	/**
	 * <p>
	 * Parse int value from string
	 * </p>
	 * 
	 * @param value
	 *            string
	 * @return int value
	 */
	public static int getInt(String value) {
		if (TextUtils.isEmpty(value)) {
			return 0;
		}

		try {
			return Integer.parseInt(value.trim(), 10);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * <p>
	 * Parse float value from string
	 * </p>
	 * 
	 * @param value
	 *            string
	 * @return float value
	 */
	public static float getFloat(String value) {
		if (value == null)
			return 0f;

		try {
			return Float.parseFloat(value.trim());
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	/**
	 * <p>
	 * Parse long value from string
	 * </p>
	 * 
	 * @param value
	 *            string
	 * @return long value
	 */
	public static long getLong(String value) {
		if (value == null)
			return 0L;

		try {
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	public static void V(String msg) {
		if (sDebug) {
			Log.v(sLogTag, msg);
		}
	}

	public static void V(String msg, Throwable e) {
		if (sDebug) {
			Log.v(sLogTag, msg, e);
		}
	}

	public static void D(String msg) {
		if (sDebug) {
			Log.d(sLogTag, msg);
		}
	}

	public static void D(String msg, Throwable e) {
		if (sDebug) {
			Log.d(sLogTag, msg, e);
		}
	}

	public static void I(String msg) {
		if (sDebug) {
			Log.i(sLogTag, msg);
		}
	}

	public static void I(String msg, Throwable e) {
		if (sDebug) {
			Log.i(sLogTag, msg, e);
		}
	}

	public static void W(String msg) {
		if (sDebug) {
			Log.w(sLogTag, msg);
		}
	}

	public static void W(String msg, Throwable e) {
		if (sDebug) {
			Log.w(sLogTag, msg, e);
		}
	}

	public static void E(String msg) {
		if (sDebug) {
			Log.e(sLogTag, msg);
		}
	}

	public static void E(String msg, Throwable e) {
		if (sDebug) {
			Log.e(sLogTag, msg, e);
		}
	}

	public static String formatDate(long time) {
		if (calendar == null || calendar.get() == null) {
			calendar = new WeakReference<Calendar>(Calendar.getInstance());
		}
		Calendar target = calendar.get();
		target.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(target.getTime());
	}

	public static String getTodayDate() {
		if (calendar == null || calendar.get() == null) {
			calendar = new WeakReference<Calendar>(Calendar.getInstance());
		}
		Calendar today = calendar.get();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(today.getTime());
	}

	/**
	 * Returns whether the network is available
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.w(TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0, length = info.length; i < length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether the network is roaming
	 */
	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			// Log.w(Constants.TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			} else {
			}
		}
		return false;
	}

	/**
	 * 格式化时间（Format：yyyy-MM-dd HH:mm）
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static String formatTime(long timeInMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(timeInMillis));
	}

	/**
	 * 文件拷贝工具类
	 * 
	 * @param src
	 *            源文件
	 * @param dst
	 *            目标文件
	 * @throws IOException
	 */
	public static void copyFile(InputStream in, FileOutputStream dst) throws IOException {
		byte[] buffer = new byte[8192];
		int len = 0;
		while ((len = in.read(buffer)) > 0) {
			dst.write(buffer, 0, len);
		}
		in.close();
		dst.close();
	}

	/**
	 * 解析HTTP String Entity
	 * 
	 * @param response
	 *            HTTP Response
	 * @return 市场API返回的消息(String)
	 */
	public static String getStringResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		try {
			return entity == null ? null : EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			D("getStringResponse meet ParseException", e);
		} catch (IOException e) {
			D("getStringResponse meet IOException", e);
		}
		return null;
	}

	/**
	 * 解析HTTP InputStream Entity
	 * 
	 * @param response
	 *            HTTP Response
	 * @return 市场API返回的消息(InputStream)
	 */
	public static InputStream getInputStreamResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		try {
			if (entity == null)
				return null;
			return AndroidHttpClient.getUngzippedContent(entity);
		} catch (IllegalStateException e) {
			D("getInputStreamResponse meet IllegalStateException", e);
		} catch (IOException e) {
			D("getInputStreamResponse meet IOException", e);
		}
		return null;
	}

	/**
	 * 界面切换动画
	 * 
	 * @return
	 */
	public static LayoutAnimationController getLayoutAnimation() {
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(50);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(100);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
		return controller;
	}

	/**
	 * Check whether the SD card is readable
	 */
	public static boolean isSdcardReadable() {
		final String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) || Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether the SD card is writable
	 */
	public static boolean isSdcardWritable() {
		final String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static float sp2px(Context context, float sp) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		display.getMetrics(displaymetrics);

		final float scale = displaymetrics.scaledDensity;
		return sp * scale;
	}

	public static int dp2px(Context context, int dp) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		display.getMetrics(displaymetrics);

		return (int) (dp * displaymetrics.density + 0.5f);
	}

	public static int px2dp(Context context, int px) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		display.getMetrics(displaymetrics);

		return (int) (px / displaymetrics.density + 0.5f);
	}

	public static int getScrollY(ListView lv) {
		View c = lv.getChildAt(0);
		if (c == null) {
			return 0;
		}

		int firstVisiblePosition = lv.getFirstVisiblePosition();
		int top = c.getTop();

		int scrollY = -top + firstVisiblePosition * c.getHeight();
		return scrollY;
	}

	public static int getScrollY(AbsListView lv) {
		View c = lv.getChildAt(0);
		if (c == null) {
			return 0;
		}

		int firstVisiblePosition = lv.getFirstVisiblePosition();
		int scrollY = -(c.getTop());
		// int scrollY = 0;

		sListViewItemHeights.put(lv.getFirstVisiblePosition(), c.getHeight());

		// if(scrollY>0)
		// Log.d("QuickReturnUtils", "getScrollY() : -(c.getTop()) - "+ -(c.getTop()));
		// else
		// Log.i("QuickReturnUtils", "getScrollY() : -(c.getTop()) - "+ -(c.getTop()));

		if (scrollY < 0)
			scrollY = 0;

		for (int i = 0; i < firstVisiblePosition; ++i) {
			// Log.d("QuickReturnUtils", "getScrollY() : i - "+i);

			// Log.d("QuickReturnUtils", "getScrollY() : sListViewItemHeights.get(i) - "+sListViewItemHeights.get(i));

			if (sListViewItemHeights.get(i) != null) // (this is a sanity check)
				scrollY += sListViewItemHeights.get(i); // add all heights of the views that are gone

		}

		// Log.d("QuickReturnUtils", "getScrollY() : scrollY - "+scrollY);

		return scrollY;
	}

	/**
	 * Convert a translucent themed Activity {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque Activity.
	 * <p>
	 * Call this whenever the background of a translucent Activity has changed to become opaque. Doing so will allow the {@link android.view.Surface} of the Activity behind to be released.
	 * <p>
	 * This call has no effect on non-translucent activities or on activities with the {@link android.R.attr#windowIsFloating} attribute.
	 */
	public static void convertActivityFromTranslucent(Activity activity) {
		try {
			Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
			method.setAccessible(true);
			method.invoke(activity);
		} catch (Throwable t) {
		}
	}

	/**
	 * Convert a translucent themed Activity {@link android.R.attr#windowIsTranslucent} back from opaque to translucent following a call to {@link #convertActivityFromTranslucent(android.app.Activity)} .
	 * <p>
	 * Calling this allows the Activity behind this one to be seen again. Once all such Activities have been redrawn
	 * <p>
	 * This call has no effect on non-translucent activities or on activities with the {@link android.R.attr#windowIsFloating} attribute.
	 */
	public static void convertActivityToTranslucent(Activity activity) {
		try {
			Class<?>[] classes = Activity.class.getDeclaredClasses();
			Class<?> translucentConversionListenerClazz = null;
			for (Class clazz : classes) {
				if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
					translucentConversionListenerClazz = clazz;
				}
			}
			Method method = Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz);
			method.setAccessible(true);
			method.invoke(activity, new Object[] { null });
		} catch (Throwable t) {
		}
	}

	/*
	 * 获取android当前可用内存大小 
	 */
	public static long getAvailMemory() {

		ActivityManager am = (ActivityManager) SLAppication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		return mi.availMem;// 将获取的内存大小规格化
	}

	public static long getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		return initial_memory;// Byte转换为KB或者MB，内存大小规格化
	}

	public static void clearBackMemory() {
		ActivityManager activityManager = (ActivityManager) SLAppication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		int currentProcessId = android.os.Process.myPid();
		RunningAppProcessInfo runningAppProcessInfo = null;
		List<RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
		if (runningAppProcessInfos != null) {
			for (int i = 0; i < runningAppProcessInfos.size(); ++i) {
				runningAppProcessInfo = runningAppProcessInfos.get(i);
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE
				// 的进程即为长时间未使用进程或者空进程
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE
				// 的进程都是非可见进程,即在后台运行
				if (runningAppProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE && runningAppProcessInfo.pid != currentProcessId) {
					String[] pkgList = runningAppProcessInfo.pkgList;
					for (int j = 0; j < pkgList.length; ++j) {
						activityManager.killBackgroundProcesses(pkgList[j]);
						// count++;
					}
				}
			}
		}
	}
}