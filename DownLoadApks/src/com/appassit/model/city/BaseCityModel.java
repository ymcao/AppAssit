package com.appassit.model.city;

import java.io.Serializable;

public abstract class BaseCityModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 城市ID
	 */
	protected String cityId;

	/**
	 * 城市名称
	 */
	protected String cityName;

	/**
	 * BaseCityModel 构造方法
	 */
	public BaseCityModel() {
		this.cityId = "";
		this.cityName = "";
	}

	/**
	 * BaseCityModel 构造方法
	 * 
	 * @param cityId
	 * @param cityName
	 */
	public BaseCityModel(String cityId, String cityName) {
		this.cityId = cityId;
		this.cityName = cityName;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || super.getClass() != o.getClass())
			return false;

		BaseCityModel model = (BaseCityModel) o;

		if (cityId != null ? !cityId.equals(model.cityId) : model.cityId != null)
			return false;
		if (cityName != null ? !cityName.equals(model.cityName) : model.cityName != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = cityId != null ? cityId.hashCode() : 0;
		result = 31 * result + (cityName != null ? cityName.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "BaseCity{" + "cityId='" + cityId + '\'' + ", cityName='" + cityName + '\'' + '}';
	}
}
