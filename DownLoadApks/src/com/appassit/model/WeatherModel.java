package com.appassit.model;

import java.util.List;

/**
 */
public class WeatherModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 时间 */
	public String date;

	/** 天气信息 */
	public WeatherInfo info;

	public static class WeatherInfo {

		/** 晚上 */
		public List<String> night;

		/** 白天 */
		public List<String> day;
	}
}
