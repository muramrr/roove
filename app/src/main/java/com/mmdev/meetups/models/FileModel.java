package com.mmdev.meetups.models;

/**
 * Created by Alessandro Barreto on 22/06/2016.
 */
public class FileModel {

	private String mType;
	private String mUrl;
	private String mName;
	private String mSize;

	public FileModel() {
	}

	public FileModel(String fileType, String fileUrl, String fileName, String fileSize) {
		mType = fileType;
		mUrl = fileUrl;
		mName = fileName;
		mSize = fileSize;
	}

	public String getType() {
		return mType;
	}
	public void setType(String type) {
		this.mType = type;
	}

	public String getUrl () {
		return mUrl;
	}
	public void setUrl (String url) {
		mUrl = url;
	}

	public String getFileName () {
		return mName;
	}
	public void setFileName (String fileName) {
		mName = fileName;
	}

	public String getSize_file() {
		return mSize;
	}
	public void setSize_file(String size_file) {
		mSize = size_file;
	}
}
