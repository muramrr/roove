package com.mmdev.meetups.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/* Created by A on 06.06.2019.*/

/* This is the documentation block about the class */

public class ChatModel {
	private String mMessage;
	private Date mTimestamp;
	private UserChatModel mSenderUserModel;
	private FileModel mFileModel;
	private MapModel mMapModel;

	public ChatModel () {} // Needed for Firebase

	/*
		message only constructor
	 */
	public ChatModel (UserChatModel senderUserModel, String message) {
		mSenderUserModel = senderUserModel;
		mMessage = message;
	}

	/*
		file attached constructor
	*/
	public ChatModel (UserChatModel senderUserModel, String message, FileModel file) {
		mSenderUserModel = senderUserModel;
		mMessage = message;
		mFileModel = file;
	}

	/*
		location attached constructor
	 */
	public ChatModel (UserChatModel senderUserModel,String message, MapModel mapModel) {
		mSenderUserModel = senderUserModel;
		mMessage = message;
		mMapModel = mapModel;
	}

	public String getMessage() { return mMessage; }
	public void setMessage(String message) { mMessage = message; }

	@ServerTimestamp
	public Date getTimestamp() { return mTimestamp; }
	public void setTimestamp(Date timestamp) { mTimestamp = timestamp; }

	public UserChatModel getSenderUserModel() { return mSenderUserModel; }
	public void setSenderUserModel (UserChatModel senderUserModel) { mSenderUserModel = senderUserModel; }

	public FileModel getFileModel () { return mFileModel; }
	public void setFileModel (FileModel file) { mFileModel = file; }

	public MapModel getMapModel() { return mMapModel; }
	public void setMapModel(MapModel mapModel) { mMapModel = mapModel; }
}
