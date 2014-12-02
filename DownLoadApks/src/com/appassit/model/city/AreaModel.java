package com.appassit.model.city;

public class AreaModel extends BaseCityModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 天气ID
	 */
	private String weatherCode;

	/**
	 * AreaModel构造方法
	 */
	public AreaModel() {
		super();
		this.weatherCode = "";
	}

	/**
	 * AreaModel构造方法
	 * 
	 * @param cityId
	 * @param cityName
	 * @param weatherCode
	 */
	public AreaModel(String cityId, String cityName, String weatherCode) {
		super(cityId, cityName);
		this.weatherCode = weatherCode;
	}

	public String getWeatherCode() {
		return weatherCode;
	}

	public void setWeatherCode(String weatherCode) {
		this.weatherCode = weatherCode;
	}

	@Override
	public String toString() {
		return "AreaModel{" + "weatherCode='" + weatherCode + '\'' + '}';
	}
}
