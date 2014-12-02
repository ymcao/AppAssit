package com.appassit.model;

import java.io.File;

import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.http.HttpHandler;

public class DownloadInfo implements IBaseModel {

	private long id;
	private int apkid;
	@Transient
	private HttpHandler<File> handler;

	private HttpHandler.State state;
	private int i_state;
	private String downloadUrl;

	private String fileName;

	private String fileSavePath;

	private long progress;

	private long fileLength;

	private boolean autoResume;

	private boolean autoRename;

	private boolean isAddDownload;

	private String iconPath;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public HttpHandler<File> getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler<File> handler) {
		this.handler = handler;
	}

	public HttpHandler.State getState() {
		return state;
	}

	public void setState(HttpHandler.State state) {
		this.state = state;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSavePath() {
		return fileSavePath;
	}

	public void setFileSavePath(String fileSavePath) {
		this.fileSavePath = fileSavePath;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public boolean isAutoResume() {
		return autoResume;
	}

	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	public boolean isAutoRename() {
		return autoRename;
	}

	public void setAutoRename(boolean autoRename) {
		this.autoRename = autoRename;
	}

	public boolean isAddDownload() {
		return isAddDownload;
	}

	public void setAddDownload(boolean isAddDownload) {
		this.isAddDownload = isAddDownload;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public int getApkid() {
		return apkid;
	}

	public void setApkid(int apkid) {
		this.apkid = apkid;
	}

	public int getI_state() {
		return i_state;
	}

	public void setI_state(int i_state) {
		this.i_state = i_state;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DownloadInfo))
			return false;

		DownloadInfo that = (DownloadInfo) o;

		if (id != that.id)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}

}
