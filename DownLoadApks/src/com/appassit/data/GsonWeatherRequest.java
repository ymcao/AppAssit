package com.appassit.data;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonWeatherRequest<T> extends Request<T> {

	protected final static String TAG = "GsonRequest";
	public static final int TIMEOUT_ERROR = 600;
	public static final int BUSSINESS_ERROR = 610;

	private final Gson mGson = new Gson();
	private final Class<T> mClazz;
	private final Response.Listener<T> mListener;
	private final Map<String, String> mHeaders;

	public GsonWeatherRequest(String url, Class<T> clazz,
			Response.Listener<T> listener, Response.ErrorListener errorListener) {
		this(Method.GET, url, clazz, null, listener, errorListener);
	}

	public GsonWeatherRequest(int method, String url, Class<T> clazz,
			Map<String, String> headers, Response.Listener<T> listener,
			Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mClazz = clazz;
		this.mListener = listener;
		this.mHeaders = headers;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = toJson(new String(response.data,
					HttpHeaderParser.parseCharset(response.headers)));
			return Response.success(mGson.fromJson(json, mClazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(T t) {
		mListener.onResponse(t);
	}

	/** 将String转换成Json */
	protected String toJson(String str) {
		return "{\"data\":"
				+ str.substring(str.lastIndexOf("(") + 1, str.lastIndexOf(")"))
				+ "}";
	}
}
