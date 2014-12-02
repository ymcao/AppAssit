package com.appassit.model;

import com.google.gson.annotations.SerializedName;

/**

 */
public class Weather extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 图片ID */
	public int img;

	/** 时间 */
	public int hour;

	/** 温度 */
	@SerializedName("temperature")
	public int temp;

	/** 华氏度 */
	public int humidity;

	/** 描述信息 */
	public String info;
}
