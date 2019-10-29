package com.mmdev.data.user

import android.content.Context
import android.util.Log
import com.mmdev.business.user.model.UserItem
import com.mmdev.business.user.repository.UserRepository
import com.mmdev.data.utils.TinyDB
import javax.inject.Inject
import javax.inject.Singleton

/* Created by A on 01.09.2019.*/

/**
 * This is the documentation block about the class
 */

@Singleton
class UserRepositoryImpl @Inject constructor(private val context: Context): UserRepository {


	companion object{
		private const val PREF_KEY_GENERAL_USER_STORE = "userModel"
		private const val PREF_KEY_GENERAL_IF_SAVED = "saved"
		private const val PREF_KEY_CURRENT_USER_NAME = "name"
		private const val PREF_KEY_CURRENT_USER_CITY = "city"
		private const val PREF_KEY_CURRENT_USER_GENDER = "gender"
		private const val PREF_KEY_CURRENT_USER_P_GENDER = "preferedGender"
		private const val PREF_KEY_CURRENT_USER_MAIN_PIC_URL = "mainphotourl"
		private const val PREF_KEY_CURRENT_USER_PHOTO_URLS = "photourls"
		private const val PREF_KEY_CURRENT_USER_ID = "uid"
	}

	override fun getSavedUser(): UserItem {
		val prefs = TinyDB(PREF_KEY_GENERAL_USER_STORE, context)
		return if (prefs.getBoolean(PREF_KEY_GENERAL_IF_SAVED, false)) {
			try {
				val name = prefs.getString(PREF_KEY_CURRENT_USER_NAME , "")
				val city = prefs.getString(PREF_KEY_CURRENT_USER_CITY, "")
				val gender = prefs.getString(PREF_KEY_CURRENT_USER_GENDER, "")
				val preferedGender = prefs.getString(PREF_KEY_CURRENT_USER_P_GENDER, "")
				val mainPhotoUrl = prefs.getString(PREF_KEY_CURRENT_USER_MAIN_PIC_URL, "")
				val photoUrls = prefs.getListString(PREF_KEY_CURRENT_USER_PHOTO_URLS)
				val uid = prefs.getString(PREF_KEY_CURRENT_USER_ID, "")
				Log.wtf("mylogs", "retrieved user info from sharedpref successfully")
				UserItem(name, city, gender, preferedGender, mainPhotoUrl, photoUrls, uid)
			}finally {
				UserItem(name = "local internal error")
			}

		}
		else {
			Log.wtf("mylogs", "Can't get user, seems it is not saved")
			UserItem(name = "no user saved")
		}
	}


	override fun saveUserInfo(currentUserItem: UserItem) {
		val prefs = TinyDB(PREF_KEY_GENERAL_USER_STORE, context)
		prefs.clear()
		prefs.putString(PREF_KEY_CURRENT_USER_NAME, currentUserItem.name)
		prefs.putString(PREF_KEY_CURRENT_USER_CITY, currentUserItem.city)
		prefs.putString(PREF_KEY_CURRENT_USER_GENDER, currentUserItem.gender)
		prefs.putString(PREF_KEY_CURRENT_USER_P_GENDER, currentUserItem.preferedGender)
		prefs.putString(PREF_KEY_CURRENT_USER_MAIN_PIC_URL, currentUserItem.mainPhotoUrl)
		prefs.putListString(PREF_KEY_CURRENT_USER_PHOTO_URLS, currentUserItem.photoURLs!!)
		prefs.putString(PREF_KEY_CURRENT_USER_ID, currentUserItem.userId)
		prefs.putBoolean(PREF_KEY_GENERAL_IF_SAVED, true)
		Log.wtf("mylogs", "User successfully saved")
	}

}