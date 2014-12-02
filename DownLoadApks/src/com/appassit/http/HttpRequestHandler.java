package com.appassit.http;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import com.appassit.model.IBaseModel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

public class HttpRequestHandler implements Runnable {

	public static final int TIMEOUT_ERROR = 600;

	public static final int BUSSINESS_ERROR = 610;

	private RequestMethod requestMethod;

	private WeakReference<ApiRequestListener> weakListener;

	private String baseUrl;

	private int code = 200;

	private IBaseModel result = null;
	// 用于确保和服务器加密的顺序一致
	private ArrayList<BasicNameValuePair> list;
	private Bundle cValues = new Bundle();

	private HttpRequestHandler(String url, RequestMethod requestMethod, ApiRequestListener listener, Bundle values) {
		init(url, requestMethod, listener, values);
	}

	private void init(String url, RequestMethod requestMethod, ApiRequestListener listener, Bundle values) {
		this.baseUrl = url;
		this.requestMethod = requestMethod;
		if (listener != null) {
			this.weakListener = new WeakReference<ApiRequestListener>(listener);
		}
		initParm(values);
	}

	private void initParm(Bundle values) {
		list = new ArrayList<BasicNameValuePair>();
		StringBuilder builder = new StringBuilder();
		BasicNameValuePair bvp = null;
		if (values == null || values.size() == 0) {
			bvp = new BasicNameValuePair("sign_parm", "sign");
			list.add(bvp);
			builder.append(bvp.getValue());
		} else {
			Set<String> keys = values.keySet();
			for (String key : keys) {
				if (values.get(key) == null)
					continue;
				String value = values.get(key).toString();
				if (TextUtils.isEmpty(value)) {
					continue;
				}
				bvp = new BasicNameValuePair(key, value);
				list.add(bvp);
				builder.append(value);
			}
			cValues = values;
		}
		/*String sign = builder.toString();
		String code = Md5Utils.strCode(Md5Utils.getMD5(sign));
		code = code.substring(0, code.length() - 1);
		list.add(new BasicNameValuePair("sign", code));*/
		builder = null;
	}

	@Override
	public void run() {
		HttpUriRequest request = null;
		HttpResponse response = null;
		try {
			request = ApiRequestFactory.getRequest(baseUrl, requestMethod, list);
			response = HttpClientFactory.getHttpClient().execute(request);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == statusCode) {
				result = ApiResponseFactory.getResponse(baseUrl, response);
				if (result == null) {
					code = BUSSINESS_ERROR;
				} else {
					sendMessageAtFrontOfUIQueue();
					return;
				}
			} else {
				code = statusCode;
			}
		} catch (IOException e) {
			e.printStackTrace();
			code = TIMEOUT_ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			code = BUSSINESS_ERROR;
		} finally {
			// release the connection
			if (request != null) {
				request.abort();
			}
			if (response != null) {
				try {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						entity.consumeContent();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (code != 200) {
				sendMessageAtFrontOfUIQueue();
			}
		}

	}

	private static Handler sHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			if (msg == null || msg.obj == null || !(msg.obj instanceof HttpRequestHandler))
				return;
			HttpRequestHandler handler = (HttpRequestHandler) msg.obj;
			if (handler.weakListener != null) {
				ApiRequestListener listener = handler.weakListener.get();
				if (listener != null) {
					listener.onDataReady(handler.cValues, handler.baseUrl, handler.code, handler.result);
				}
			}
			handler.recyle();
		}

	};

	private void sendMessageAtFrontOfUIQueue() {
		Message msg = Message.obtain();
		msg.obj = this;
		sHandler.sendMessageAtFrontOfQueue(msg);
	}

	private HttpRequestHandler next;

	private static HttpRequestHandler sHeader;
	private static final int MAX_COUNT = 10;
	private static int sCount = 0;
	private static Object sLock = new Object();

	public static HttpRequestHandler obtain(String url, RequestMethod requestMethod, ApiRequestListener handler, Bundle values) {
		if (sHeader != null) {
			synchronized (sLock) {
				if (sHeader != null) {
					HttpRequestHandler head = sHeader;
					sHeader = head.next;
					head.next = null;
					sCount--;
					head.init(url, requestMethod, handler, values);
					return head;
				}
			}
		}
		return new HttpRequestHandler(url, requestMethod, handler, values);
	}

	private void recyle() {
		reset();
		if (sCount > MAX_COUNT) {
			return;
		}
		synchronized (sLock) {
			next = sHeader;
			sHeader = this;
			sCount++;
		}
	}

	private void reset() {
		code = 200;
		baseUrl = null;
		result = null;
		weakListener = null;
		if (cValues != null) {
			cValues.clear();
		}
		if (list != null) {
			list.clear();
			list = null;
		}
	};

}
