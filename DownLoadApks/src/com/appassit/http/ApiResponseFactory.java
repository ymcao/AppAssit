package com.appassit.http;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.appassit.model.Beauty;
import com.appassit.model.IBaseModel;
import com.appassit.model.PageInfo;
import com.appassit.model.Response;

public class ApiResponseFactory {

	static String TAG = "ApiResponseFactory";

	public static IBaseModel getResponse(String baseUrl, HttpResponse response) {
		String data = HttpUtils.getStringResponse(response);
		if (SLApis.GET_RECOMMEND_LIST.equals(baseUrl)) {
			return parseRecommend(data);
		}
		return null;
	}

	public static Response parseResponse(String data) {
		if (TextUtils.isEmpty(data)) {
			return null;
		}
		try {
			JSONObject jo = new JSONObject(data);
			return parseResponse(jo);
		} catch (JSONException e) {
		}
		return null;
	}

	private static Response parseResponse(JSONObject jObject) {
		if (jObject == null) {
			return null;
		}
		JSONObject jsonResponse = jObject.optJSONObject("response");
		if (jsonResponse != null) {
			//return Response.CONVERTOR.createFromJson(jsonResponse);
		}
		return null;
	}

	private static PageInfo parsePageInfo(JSONObject jObject) {
		if (jObject == null) {
			return null;
		}
		JSONObject jobj = jObject.optJSONObject("pageinfo");
		if (jobj != null) {
			//return PageInfo.CONVERTOR.createFromJson(jobj);
		}
		return null;
	}

	private static Beauty parseRecommend(String data) {
		/*if (TextUtils.isEmpty(data)) {
			return null;
		}
		try {
			JSONObject jo = new JSONObject(data);
			Beauty rm = new Beauty();
			rm.response = parseResponse(jo);
			rm.pageinfo = parsePageInfo(jo);
			JSONArray jArray = jo.optJSONArray("list");
			rm.list = BeautyInfo.CONVERTOR.newArray(jArray);
			return rm;
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		return null;
	}

}
