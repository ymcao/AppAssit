package com.appassit.model;

import java.io.Serializable;

import com.google.gson.Gson;

public abstract class BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toJson() {
		return new Gson().toJson(this);
	}
}
