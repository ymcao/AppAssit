package com.appassit.model;

import org.json.JSONObject;

public class PageInfo implements IBaseModel {
	public int count;
	public int pagecount;
	public int page;

	@Override
	public String toString() {
		return "PageInfo [count=" + count + ", pagecount=" + pagecount + ", page=" + page + "]";
	}

	public boolean hasNext() {
		return page < pagecount;
	}

	/*public static IConvertJson.IConvertModel<PageInfo> CONVERTOR = new AbsConvertModel<PageInfo>() {

		@Override
		public PageInfo createFromJson(JSONObject json) {
			PageInfo pi = new PageInfo();
			pi.count = json.optInt("count");
			pi.pagecount = json.optInt("pagecount");
			pi.page = json.optInt("page");
			return pi;
		}
	};*/

}
