package com.appassit.model;

import java.util.ArrayList;

public class Beauty implements IBaseModel {
	public Response response;
	public ArrayList<BeautyInfo> list;
	public PageInfo pageinfo;

	@Override
	public String toString() {
		return response.toString() + list.toString() + pageinfo.toString();
	}
}
