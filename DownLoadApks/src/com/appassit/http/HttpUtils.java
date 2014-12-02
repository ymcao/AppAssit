package com.appassit.http;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	/**
	 * GET方式请求
	 */
	public static final int METHOD_GET = 1;

	/**
	 * POST方式请求
	 */
	public static final int METHOD_POST = 2;

	public static String getStringResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		try {
			// Log.i(TAG, entity == null ? null : EntityUtils.toString(entity));
			return entity == null ? null : EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getInputStreamResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		try {
			if (entity == null)
				return null;
			return AndroidHttpClient.getUngzippedContent(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
