package com.appassit.model.city;

import java.util.ArrayList;
import java.util.List;

public class CityModel extends BaseCityModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 下级区/县
	 */
	private List<AreaModel> areaModels;

	public CityModel() {
		areaModels = new ArrayList<AreaModel>();
	}

	public List<AreaModel> getAreaModels() {
		return areaModels;
	}

	public void setAreaModels(List<AreaModel> areaModels) {
		this.areaModels = areaModels;
	}

	@Override
	public String toString() {
		return "CityModel{" + "areaModels=" + areaModels + '}';
	}
}
