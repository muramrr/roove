/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.user

import android.util.Log
import com.ironz.binaryprefs.Preferences
import com.mmdev.business.user.model.UserItem
import com.mmdev.business.user.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class UserRepositoryLocal @Inject constructor(private val prefs: Preferences):
		UserRepository.LocalUserRepository {


	companion object{
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

		return if (prefs.getBoolean(PREF_KEY_GENERAL_IF_SAVED, false)) {
			try {
				val name = prefs.getString(PREF_KEY_CURRENT_USER_NAME , "")!!
				val city = prefs.getString(PREF_KEY_CURRENT_USER_CITY, "")!!
				val gender = prefs.getString(PREF_KEY_CURRENT_USER_GENDER, "")!!
				val preferedGender = prefs.getString(PREF_KEY_CURRENT_USER_P_GENDER, "")!!
				val mainPhotoUrl = prefs.getString(PREF_KEY_CURRENT_USER_MAIN_PIC_URL, "")!!
				val photoUrls = prefs.getStringSet(PREF_KEY_CURRENT_USER_PHOTO_URLS, setOf(""))!!
				val uid = prefs.getString(PREF_KEY_CURRENT_USER_ID, "")!!
				Log.wtf("mylogs", "retrieved user info from sharedpref successfully")
				UserItem(name, city, gender, preferedGender, mainPhotoUrl, photoUrls.toList(), uid)
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
		val editor = prefs.edit()
		editor.putString(PREF_KEY_CURRENT_USER_NAME, currentUserItem.name)
		editor.putString(PREF_KEY_CURRENT_USER_CITY, currentUserItem.city)
		editor.putString(PREF_KEY_CURRENT_USER_GENDER, currentUserItem.gender)
		editor.putString(PREF_KEY_CURRENT_USER_P_GENDER, currentUserItem.preferedGender)
		editor.putString(PREF_KEY_CURRENT_USER_MAIN_PIC_URL, currentUserItem.mainPhotoUrl)
		editor.putStringSet(PREF_KEY_CURRENT_USER_PHOTO_URLS, currentUserItem.photoURLs.toSet())
		editor.putString(PREF_KEY_CURRENT_USER_ID, currentUserItem.userId)
		editor.putBoolean(PREF_KEY_GENERAL_IF_SAVED, true)
		editor.apply()
		Log.wtf("mylogs", "User successfully saved")
	}

}