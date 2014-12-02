package com.appassit.model;


public class Response implements IBaseModel {
	public int code;
	public String message;

	/*public static final IConvertModel<Response> CONVERTOR = new AbsConvertModel<Response>() {

		@Override
		public Response createFromJson(JSONObject json) {
			Response response = new Response();
			response.code = json.optInt("code", -1);
			response.message = json.optString("message", "");
			return response;

		}
	};*/

}
