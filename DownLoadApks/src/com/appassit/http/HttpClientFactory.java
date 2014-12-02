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
package com.appassit.http;

import java.util.WeakHashMap;

/**
 * This is http client factory class.<br>
 * 
 * You can fetch http client by using get***HttpClient method easily.
 */
public class HttpClientFactory {

	private static String LAUNCHER_CLIENT = "launcher";

	static WeakHashMap<String, AndroidHttpClient> mHttpClientMap = new WeakHashMap<String, AndroidHttpClient>(
			1);

	/**
	 * Get the http client for MARKET module
	 * 
	 * @param userAgent
	 *            customize user agent
	 * @return android http client contains some default settings for android
	 *         device
	 */
	public static synchronized AndroidHttpClient getHttpClient() {

		AndroidHttpClient client = mHttpClientMap.get(LAUNCHER_CLIENT);
		if (client != null) {
			return client;
		}

		client = AndroidHttpClient.newInstance("");
		mHttpClientMap.put(LAUNCHER_CLIENT, client);
		return client;
	}

	/**
	 * update the G-Header
	 * 
	 * @param gHeader
	 */
	public void updateMarketHeader(String gHeader) {
		AndroidHttpClient client = mHttpClientMap.get(LAUNCHER_CLIENT);
		if (client != null) {
			client.getParams().setParameter("G-Header", gHeader);
		}
	}

	/**
	 * Must close all http clients when application is closed
	 */
	public synchronized void close() {
		AndroidHttpClient client;
		if (mHttpClientMap.containsKey(LAUNCHER_CLIENT)) {
			client = mHttpClientMap.get(LAUNCHER_CLIENT);
			if (client != null) {
				client.close();
				client = null;
			}
		}
		mHttpClientMap.clear();
	}
}