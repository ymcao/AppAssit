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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * 这个类是获取API请求内容的工厂方法
 */
public class ApiRequestFactory {

	/**
	 * 获取Market API HttpReqeust
	 */
	public static HttpUriRequest getRequest(String url, RequestMethod requestMethod, ArrayList<BasicNameValuePair> cv) throws IOException {

		if (requestMethod == RequestMethod.GET) {
			String finalUrl = getGetRequestUrl(url, cv);
			HttpGet httpGet = new HttpGet(finalUrl);
			return httpGet;
		} else if (requestMethod == RequestMethod.POST) {
			HttpEntity entity = getPostRequestEntity(cv);
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			return request;
		} else {
			return null;
		}
	}

	/**
	 * 生产GET请求的url
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private static String getGetRequestUrl(String url, ArrayList<BasicNameValuePair> params) {
		if (params == null || params.size() == 0) {
			return url;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(url).append("?");
		for (BasicNameValuePair entry : params) {
			Object obj = entry.getValue();
			if (obj == null)
				continue;
			builder.append(entry.getName()).append("=").append(entry.getValue()).append("&");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();

	}

	/**
	 * 生产POST请求的HttpEntity
	 * 
	 * @param params
	 *            请求参数
	 * @throws UnsupportedEncodingException
	 *             假如不支持UTF8编码方式会抛出此异常
	 */
	private static HttpEntity getPostRequestEntity(ArrayList<BasicNameValuePair> params) throws UnsupportedEncodingException {
		if (params == null || params.isEmpty())
			return null;

		return new UrlEncodedFormEntity(params, HTTP.UTF_8);
	}
}
