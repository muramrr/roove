package com.mmdev.meetapp.ui.main;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mmdev.meetapp.models.ProfileModel;
import com.mmdev.meetapp.utils.TinyDB;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel
{
	private MutableLiveData<ProfileModel> mProfileModel = new MutableLiveData<>();
	public MutableLiveData<String> Name = new MutableLiveData<>();
	private MutableLiveData<Integer> busy = new MutableLiveData<>();

	public ProfileViewModel() {  }

	public void init() {
		busy.setValue(View.GONE);
	}

	public MutableLiveData<Integer> getBusy() { return busy; }
	private void setBusy(int visibility) { busy.setValue(visibility); }

	/*
	check if already attached - return our viewmodel
	else check if stored profile in sharedprefs and return it
	 */
	public MutableLiveData<ProfileModel> getProfileModel (Context context) {
		if (mProfileModel.getValue() != null)
			return mProfileModel;
		if (getSavedProfile(context) != null)
			mProfileModel.setValue(getSavedProfile(context));
		return mProfileModel;
	}

	public void setProfileModel (ProfileModel profileModel) { mProfileModel.setValue(profileModel); }

	public void onLoginClicked() {
		setBusy(View.VISIBLE);
		FirebaseUser signedInUser = FirebaseAuth.getInstance().getCurrentUser();
		if (signedInUser != null && !TextUtils.isEmpty(signedInUser.getDisplayName())) {
			new Handler().postDelayed(() -> {
//                mProfileModel mProfileModel = new mProfileModel(signedInUser.getDisplayName(),"Kyiv", "male", signedInUser.getEmail());
//                setProfileModel(mProfileModel);
				Name.setValue(signedInUser.getDisplayName());
				setBusy(View.GONE);
			}, 1000);
		}
	}

	/*
		get user info from sharedPrefs
	 */
	private ProfileModel getSavedProfile(Context context) {
		TinyDB prefs = new TinyDB("profileModel", context);
		if (prefs.getBoolean("saved", false)){
			String mName = prefs.getString("name", "");
			String mCity = prefs.getString("city", "");
			String mGender = prefs.getString("gender", "");
			String mPreferedGender = prefs.getString("preferedGender", "");
			ArrayList<String> mPhotoUrls = prefs.getListString("photourls");
			String mMainPhotoUrl = prefs.getString("mainphotourl","");
			String mUID = prefs.getString("uid", "");
			return new ProfileModel(mName, mCity, mGender, mPreferedGender, mMainPhotoUrl, mPhotoUrls, mUID);
		}
		else {
			Log.d("logs", "Can't get user, seems it is not saved");
			return null;
		}
	}

	public void saveProfile(Context context, ProfileModel profileModel){
		TinyDB prefs = new TinyDB("profileModel", context);
		prefs.clear();
		prefs.putString("name", profileModel.getName());
		prefs.putString("city", profileModel.getCity());
		prefs.putString("gender", profileModel.getGender());
		prefs.putString("preferedGender", profileModel.getPreferedGender());
		prefs.putString("mainphotourl", profileModel.getMainPhotoUrl());
		prefs.putListString("photourls", profileModel.getPhotoURLs());
		prefs.putString("uid", profileModel.getUserId());
		prefs.putBoolean("saved", true);
	}

	@Override
	protected void onCleared () {
		super.onCleared();
		mProfileModel.setValue(null);
		Name.setValue(null);
	}
}
