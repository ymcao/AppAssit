package com.appassit.model;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class ApkUrlParser implements UrlParser {

	@Override
	public ArrayList<DownloadInfo> parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		ArrayList<DownloadInfo> urls = null;
		DownloadInfo url = null;
		// XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		// XmlPullParser parser = factory.newPullParser();
		XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
		parser.setInput(is, "UTF-8"); // 设置输入流 并指明编码方式

		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				urls = new ArrayList<DownloadInfo>();
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("apk")) {
					url = new DownloadInfo();
				} else if (parser.getName().equals("id")) {
					eventType = parser.next();
					url.setApkid(Integer.parseInt(parser.getText()));
				} else if (parser.getName().equals("name")) {
					eventType = parser.next();
					url.setFileName(parser.getText());
				} else if (parser.getName().equals("icon")) {
					eventType = parser.next();
					url.setIconPath(parser.getText());
				} /*else if (parser.getName().equals("status")) {
					eventType = parser.next();
					url.setStatus(Integer.parseInt(parser.getText()));
					}*/else if (parser.getName().equals("path")) {
					eventType = parser.next();
					url.setDownloadUrl(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("apk")) {
					urls.add(url);
					url = null;
				}
				break;
			}
			eventType = parser.next();
		}
		return urls;

	}
}
