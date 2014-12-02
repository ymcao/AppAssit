package com.appassit.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


public abstract class AbsConvertModel<T extends IBaseModel> implements IConvertJson.IConvertModel<T> {

	@Override
	public ArrayList<T> newArray(JSONArray jArray) {
		// TODO Auto-generated method stub
		if (jArray == null) {
			return null;
		}
		ArrayList<T> list = new ArrayList<T>();
		int length = jArray.length();
		for (int i = 0; i < length; i++) {
			JSONObject jObj = jArray.optJSONObject(i);
			if (jObj == null) {
				continue;
			}
			T t = createFromJson(jObj);
			if (t == null) {
				continue;
			}
			list.add(t);
		}
		return list;
	}

}
