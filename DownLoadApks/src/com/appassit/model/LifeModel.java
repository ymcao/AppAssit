package com.appassit.model;

/**
 * 生活信息
 */
public class LifeModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 时间 */
	public String date;

	/** 信息 */
	public LifeInfo info;

	public static class LifeInfo {
		/** 空调 */
		public String kongtiao[];

		/** 运动 */
		public String yundong[];

		/** 紫外线 */
		public String ziwaixian[];

		/** 感冒 */
		public String ganmao[];

		/** 洗车 */
		public String xiche[];

		/** 污染 */
		public String wuran[];

		/** 穿衣 */
		public String chuanyi[];
	}
}
