package com.mmdev.data.user

import android.content.Context
import android.util.Log
import com.mmdev.data.utils.TinyDB
import com.mmdev.domain.core.model.User
import javax.inject.Inject
import javax.inject.Singleton

/* Created by A on 01.09.2019.*/

/**
 * This is the documentation block about the class
 */

@Singleton
class UserRepository @Inject constructor(private val context: Context){


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

	fun getSavedUser(): User? {
		val prefs = TinyDB(PREF_KEY_GENERAL_USER_STORE, context)
		return if (prefs.getBoolean(PREF_KEY_GENERAL_IF_SAVED, false)) {
			val name = prefs.getString(PREF_KEY_CURRENT_USER_NAME , "")
			val city = prefs.getString(PREF_KEY_CURRENT_USER_CITY, "")
			val gender = prefs.getString(PREF_KEY_CURRENT_USER_GENDER, "")
			val preferedGender = prefs.getString(PREF_KEY_CURRENT_USER_P_GENDER, "")
			val mainPhotoUrl = prefs.getString(PREF_KEY_CURRENT_USER_MAIN_PIC_URL, "")
			val photoUrls = prefs.getListString(PREF_KEY_CURRENT_USER_PHOTO_URLS)
			val uid = prefs.getString(PREF_KEY_CURRENT_USER_ID, "")
			User(name, city, gender, preferedGender, mainPhotoUrl, photoUrls, uid)
		}
		else {
			Log.wtf("mylogs", "Can't get user, seems it is not saved")
			null
		}
	}


	fun saveUserInfo(currentUser: User) {
		val prefs = TinyDB(PREF_KEY_GENERAL_USER_STORE, context)
		prefs.clear()
		prefs.putString(PREF_KEY_CURRENT_USER_NAME, currentUser.name)
		prefs.putString(PREF_KEY_CURRENT_USER_CITY, currentUser.city)
		prefs.putString(PREF_KEY_CURRENT_USER_GENDER, currentUser.gender)
		prefs.putString(PREF_KEY_CURRENT_USER_P_GENDER, currentUser.preferedGender)
		prefs.putString(PREF_KEY_CURRENT_USER_MAIN_PIC_URL, currentUser.mainPhotoUrl)
		prefs.putListString(PREF_KEY_CURRENT_USER_PHOTO_URLS, currentUser.photoURLs!!)
		prefs.putString(PREF_KEY_CURRENT_USER_ID, currentUser.userId)
		prefs.putBoolean(PREF_KEY_GENERAL_IF_SAVED, true)
		Log.wtf("mylogs", "User successfully saved")
	}

}