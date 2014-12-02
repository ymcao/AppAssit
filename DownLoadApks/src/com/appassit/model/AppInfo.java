package com.appassit.model;

import android.graphics.drawable.Drawable;

/**
 * APP Bean
 */
public class AppInfo {
	private Drawable icon; // 程序图标
	private String name; // 程序名
	private String packname; // 程序包名
	private boolean inRom; // 是否安装在手机内存
	private boolean userApp; // 是否是用户程序
	private int uid; // 程序用户id
	public String rx; // 接受的流量
	public String tx; // 发送的流量
	public String totalx; // 总共流量
	public String md5 = "";
	public String sourceDir = "";

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackname() {
		return packname;
	}

	public void setPackname(String packname) {
		this.packname = packname;
	}

	public boolean isInRom() {
		return inRom;
	}

	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}

	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getRx() {
		return rx;
	}

	public void setRx(String rx) {
		this.rx = rx;
	}

	public String getTx() {
		return tx;
	}

	public void setTx(String tx) {
		this.tx = tx;
	}

	public String getTotalx() {
		return totalx;
	}

	public void setTotalx(String totalx) {
		this.totalx = totalx;
	}

	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packname=" + packname + ", inRom=" + inRom + ", userApp=" + userApp + "]";
	}

}
