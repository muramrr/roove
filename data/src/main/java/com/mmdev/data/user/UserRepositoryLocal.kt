/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.01.20 19:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.user

import android.util.Log
import com.google.gson.Gson
import com.ironz.binaryprefs.Preferences
import com.mmdev.business.base.BasePlaceInfo
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.business.user.repository.LocalUserRepository
import javax.inject.Inject
import javax.inject.Singleton


/**
 * This is the documentation block about the class
 */

@Singleton
class UserRepositoryLocal @Inject constructor(private val prefs: Preferences):
		LocalUserRepository {

	val gson = Gson()

	companion object{
		private const val PREF_KEY_GENERAL_IF_SAVED = "saved"
		private const val PREF_KEY_CURRENT_USER_NAME = "name"
		private const val PREF_KEY_CURRENT_USER_AGE = "age"
		private const val PREF_KEY_CURRENT_USER_CITY = "city"
		private const val PREF_KEY_CURRENT_USER_GENDER = "gender"
		private const val PREF_KEY_CURRENT_USER_MAIN_PHOTO_URL = "mainphotourl"
		private const val PREF_KEY_CURRENT_USER_ID = "uid"
		private const val PREF_KEY_CURRENT_USER_P_GENDER = "preferedGender"
		private const val PREF_KEY_CURRENT_USER_PHOTO_URLS = "photourls"
		private const val PREF_KEY_CURRENT_USER_PLACES_ID = "placesToGo"


		private const val TAG = "mylogs_UserRepoImpl"
	}

	override fun getSavedUser(): UserItem? {
		return if (prefs.getBoolean(PREF_KEY_GENERAL_IF_SAVED, false)) {
			try {
				val name = prefs.getString(PREF_KEY_CURRENT_USER_NAME , "")!!
				val age = prefs.getInt(PREF_KEY_CURRENT_USER_AGE, 18)
				val city = prefs.getString(PREF_KEY_CURRENT_USER_CITY, "")!!
				val gender = prefs.getString(PREF_KEY_CURRENT_USER_GENDER, "")!!
				val uid = prefs.getString(PREF_KEY_CURRENT_USER_ID, "")!!
				val mainPhotoUrl = prefs.getString(PREF_KEY_CURRENT_USER_MAIN_PHOTO_URL, "")!!
				val preferredGender = prefs.getString(PREF_KEY_CURRENT_USER_P_GENDER, "")!!
				val photoUrls = prefs.getStringSet(PREF_KEY_CURRENT_USER_PHOTO_URLS, setOf(""))!!
				val placesToGoStrings = prefs.getStringSet(PREF_KEY_CURRENT_USER_PLACES_ID, setOf(""))!!
				val placesToGoItems = mutableListOf<BasePlaceInfo>()
				for (placeToGoSting in placesToGoStrings)
					placesToGoItems.add(gson.fromJson(placeToGoSting, BasePlaceInfo::class.java))

				Log.wtf(TAG, "retrieved user info from sharedpref successfully")

				UserItem(BaseUserInfo(name,
				                      age,
				                      city,
				                      gender,
				                      mainPhotoUrl,uid),
				         preferredGender,
				         photoUrls.toMutableList(),
				         placesToGoItems
				)
			}catch (e: Exception) {
				Log.wtf(TAG, "read exception, but user is saved")
				prefs.edit().clear().commit()
				null
			}
		}
		else {
			Log.wtf(TAG, "User is not saved")
			null
		}
	}


	override fun saveUserInfo(userItem: UserItem) {

		val editor = prefs.edit()
		editor.putBoolean(PREF_KEY_GENERAL_IF_SAVED, true)

		editor.putString(PREF_KEY_CURRENT_USER_NAME, userItem.baseUserInfo.name)
		editor.putInt(PREF_KEY_CURRENT_USER_AGE, userItem.baseUserInfo.age)
		editor.putString(PREF_KEY_CURRENT_USER_CITY, userItem.baseUserInfo.city)
		editor.putString(PREF_KEY_CURRENT_USER_GENDER, userItem.baseUserInfo.gender)
		editor.putString(PREF_KEY_CURRENT_USER_MAIN_PHOTO_URL, userItem.baseUserInfo.mainPhotoUrl)
		editor.putString(PREF_KEY_CURRENT_USER_ID, userItem.baseUserInfo.userId)
		editor.putString(PREF_KEY_CURRENT_USER_P_GENDER, userItem.preferredGender)
		editor.putStringSet(PREF_KEY_CURRENT_USER_PHOTO_URLS, userItem.photoURLs.toSet())

		val placesToGoList = mutableListOf<String>()
		for (place in userItem.placesToGo)
			placesToGoList.add(gson.toJson(place))

		editor.putStringSet(PREF_KEY_CURRENT_USER_PLACES_ID, placesToGoList.toSet())

		editor.commit()
		Log.wtf(TAG, "User successfully saved")
	}

}