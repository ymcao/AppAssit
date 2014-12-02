package com.appassit.model;

import java.io.InputStream;
import java.util.ArrayList;

public interface UrlParser {
	/**
	 * 解析输入流 得到Url对象集合
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public ArrayList<DownloadInfo> parse(InputStream is) throws Exception;

	/**
	 * 序列化Url对象集合 得到XML形式的字符串
	 * 
	 * @param books
	 * @return
	 * @throws Exception
	 */
	// public String serialize(ArrayList<UrlModel> urls) throws Exception;

}
