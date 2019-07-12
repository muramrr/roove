package com.mmdev.meetups.models;

import java.util.ArrayList;

public class ProfileModel
{

	private String mName;
	private String mCity;
	private String mGender;
	private String mPreferedGender;
	private String mMainPhotoUrl;
	private ArrayList<String> mPhotoUrls = new ArrayList<>();
	private String mUserID;

	public ProfileModel (){}

	public ProfileModel (String mName, String mCity,
						 String mGender, String mPreferedGender, String mMainPhotoUrl,
						 ArrayList<String> photoURLs, String uID) {
		this.mName = mName;
		this.mGender = mGender;
		this.mPreferedGender = mPreferedGender;
		this.mCity = mCity;
		this.mMainPhotoUrl = mMainPhotoUrl;
		mPhotoUrls.addAll(photoURLs);
		mUserID = uID;
	}

	public void setName(String name) { mName = name; }
	public String getName() { return mName; }

	public String getCity () { return mCity; }
	public void setCity (String city) { mCity = city; }

	public String getPreferedGender () { return mPreferedGender; }
	public void setPreferedGender (String preferedGender) { mPreferedGender = preferedGender; }

	public String getGender () { return mGender; }
	public void setGender (String gender) { mGender = gender; }

	public ArrayList<String> getPhotoUrls () { return mPhotoUrls; }
	public void setPhotoUrls (ArrayList<String> photoURLs) { mPhotoUrls.addAll(photoURLs); }

	public String getUserID () { return mUserID; }
	public void setUserID (String uID) { mUserID = uID; }

	public String getMainPhotoUrl () { return mMainPhotoUrl; }
	public void setMainPhotoUrl (String mainPhotoUrl) { mMainPhotoUrl = mainPhotoUrl; }


}
