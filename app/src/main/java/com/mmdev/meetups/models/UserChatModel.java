package com.mmdev.meetups.models;

import androidx.annotation.NonNull;

public class UserChatModel
{
	private String mUserID;
	private String mGender;
	private String mName;
	private String mMainPhotoUrl;

	public UserChatModel () { }

	public UserChatModel (String Name, String Gender, String MainPhotoUrl, String UserID) {
		mName = Name;
		mGender = Gender;
		mMainPhotoUrl = MainPhotoUrl;
		mUserID = UserID;
	}

	public String getName() { return mName; }
	public void setName(String name) { this.mName = name; }

	public String getGender () { return mGender; }
	public void setGender (String gender) { this.mGender = gender; }

	public String getMainPhotoUrl () { return mMainPhotoUrl; }
	public void setMainPhotoUrl (String mainPhotoUrl) { this.mMainPhotoUrl = mainPhotoUrl; }

	public String getID () { return mUserID; }
	public void setID (String id) { this.mUserID = id; }

	@NonNull
	@Override
	public String toString() {
		return "UserChatModel{" + "\n"+
				"\tname=" + mName + ",\n"+
				"\tid=" + mUserID + ",\n"+
				"\tPhotoUrl='" + mMainPhotoUrl + "\n" +
				'}';
	}
}
