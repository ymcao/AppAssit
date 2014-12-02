package com.appassit.http;

import android.os.Bundle;

/**
 * API请求监听器
 * 
 */
public interface ApiRequestListener {
	/**
	 * 
	 * @param parms
	 *            请求接口的参数 用于返回给调用页 ,区分不同的请求
	 * @param code
	 * @param result
	 */
	void onDataReady(Bundle parms, String url, int code, Object result);

}
