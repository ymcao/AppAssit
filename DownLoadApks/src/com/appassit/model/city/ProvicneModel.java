package com.appassit.model.city;

import java.util.ArrayList;
import java.util.List;

public class ProvicneModel extends BaseCityModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 城市列表
	 */
	private List<CityModel> cityModels;

	public ProvicneModel() {
		cityModels = new ArrayList<CityModel>();
	}

	public List<CityModel> getCityModels() {
		return cityModels;
	}

	public void setCityModels(List<CityModel> cityModels) {
		this.cityModels = cityModels;
	}

	@Override
	public String toString() {
		return "ProvicneModel{" + "cityModels=" + cityModels + '}';
	}
}
