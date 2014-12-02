package com.appassit.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IConvertJson {

	public JSONObject convertToJson();

	public static interface IConvertModel<T extends IBaseModel> {

		public T createFromJson(JSONObject json);

		public ArrayList<T> newArray(JSONArray jArray);
	}

}
