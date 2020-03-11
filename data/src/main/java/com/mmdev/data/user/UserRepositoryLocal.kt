/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.03.20 21:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.user

import android.util.Log
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.ironz.binaryprefs.Preferences
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.PhotoItem
import com.mmdev.business.core.PreferredAgeRange
import com.mmdev.business.core.UserItem
import com.mmdev.business.local.LocalUserRepository
import com.mmdev.business.places.BasePlaceInfo
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton


/**
 * This is the documentation block about the class
 */

@Singleton
class UserRepositoryLocal @Inject constructor(private val prefs: Preferences,
                                              private val auth: FirebaseAuth,
                                              private val fbLogin: LoginManager) :
		LocalUserRepository {

	private val gson = Gson()

	companion object{
		private const val PREF_KEY_GENERAL_IF_SAVED = "saved"
		private const val PREF_KEY_CURRENT_USER_NAME = "name"
		private const val PREF_KEY_CURRENT_USER_AGE = "age"
		private const val PREF_KEY_CURRENT_USER_CITY = "city"
		private const val PREF_KEY_CURRENT_USER_GENDER = "gender"
		private const val PREF_KEY_CURRENT_USER_MAIN_PHOTO_URL = "mainPhotoUrl"
		private const val PREF_KEY_CURRENT_USER_ID = "uid"
		private const val PREF_KEY_CURRENT_USER_P_GENDER = "preferredGender"
		private const val PREF_KEY_CURRENT_USER_ABOUT_TEXT = "aboutText"
		private const val PREF_KEY_CURRENT_USER_CITY_TO_DISPLAY = "cityToDisplay"
		private const val PREF_KEY_CURRENT_USER_PHOTO_URLS = "photoUrls"
		private const val PREF_KEY_CURRENT_USER_PLACES_ID = "placesToGo"

		private const val PREF_KEY_CURRENT_USER_P_AGE_MIN = "preferredAgeMin"
		private const val PREF_KEY_CURRENT_USER_P_AGE_MAX = "preferredAgeMax"
		private const val TAG = "mylogs_UserRepoImpl"
	}

	override fun getSavedUser(): UserItem {
		return if (prefs.getBoolean(PREF_KEY_GENERAL_IF_SAVED, false)) {
			try {
				val name = prefs.getString(PREF_KEY_CURRENT_USER_NAME , "")!!
				val age = prefs.getInt(PREF_KEY_CURRENT_USER_AGE, 18)
				val city = prefs.getString(PREF_KEY_CURRENT_USER_CITY, "")!!
				val cityToDisplay = prefs.getString(PREF_KEY_CURRENT_USER_CITY_TO_DISPLAY, "")!!
				val gender = prefs.getString(PREF_KEY_CURRENT_USER_GENDER, "")!!
				val preferredGender = prefs.getString(PREF_KEY_CURRENT_USER_P_GENDER, "")!!
				val uid = prefs.getString(PREF_KEY_CURRENT_USER_ID, "")!!
				val mainPhotoUrl = prefs.getString(PREF_KEY_CURRENT_USER_MAIN_PHOTO_URL, "")!!

				val aboutText = prefs.getString(PREF_KEY_CURRENT_USER_ABOUT_TEXT, "")!!

				val photosStrings =
					JSONArray(prefs.getString(PREF_KEY_CURRENT_USER_PHOTO_URLS, "")!!)
				val photoItems = mutableListOf<PhotoItem>()
				for (i in 0 until photosStrings.length())
					photoItems.add(gson.fromJson(photosStrings.get(i).toString(),
					                             PhotoItem::class.java))

				val placesToGoStrings =
					JSONArray(prefs.getString(PREF_KEY_CURRENT_USER_PLACES_ID, "")!!)
				val placesToGoItems = mutableListOf<BasePlaceInfo>()
				for (i in 0 until placesToGoStrings.length())
					placesToGoItems.add(gson.fromJson(placesToGoStrings.get(i).toString(),
					                                  BasePlaceInfo::class.java))

				val preferredAgeMin = prefs.getInt(PREF_KEY_CURRENT_USER_P_AGE_MIN, 18)
				val preferredAgeMax =  prefs.getInt(PREF_KEY_CURRENT_USER_P_AGE_MAX, 18)

				//Log.wtf(TAG, "retrieved user info from sharedpref successfully")

				UserItem(baseUserInfo = BaseUserInfo(name,
				                                     age,
				                                     city,
				                                     gender,
				                                     preferredGender,
				                                     mainPhotoUrl,
				                                     uid),
				         aboutText = aboutText,
				         cityToDisplay = cityToDisplay,
				         photoURLs = photoItems,
				         placesToGo = placesToGoItems,
				         preferredAgeRange = PreferredAgeRange(preferredAgeMin, preferredAgeMax))

			}catch (e: Exception) {
				Log.wtf(TAG, "read exception, but user is saved")
				if (auth.currentUser != null) {
					auth.signOut()
					fbLogin.logOut()
				}
				prefs.edit().clear().commit()
				UserItem()
			}
		}
		else {
			if (auth.currentUser != null) {
				auth.signOut()
				fbLogin.logOut()
			}
			Log.wtf(TAG, "User is not saved")
			UserItem()
		}
	}


	override fun saveUserInfo(userItem: UserItem) {

		val editor = prefs.edit()
		editor.putBoolean(PREF_KEY_GENERAL_IF_SAVED, true)

		editor.putString(PREF_KEY_CURRENT_USER_NAME, userItem.baseUserInfo.name)
		editor.putInt(PREF_KEY_CURRENT_USER_AGE, userItem.baseUserInfo.age)
		editor.putString(PREF_KEY_CURRENT_USER_CITY, userItem.baseUserInfo.city)
		editor.putString(PREF_KEY_CURRENT_USER_CITY_TO_DISPLAY, userItem.cityToDisplay)
		editor.putString(PREF_KEY_CURRENT_USER_GENDER, userItem.baseUserInfo.gender)
		editor.putString(PREF_KEY_CURRENT_USER_P_GENDER, userItem.baseUserInfo.preferredGender)
		editor.putString(PREF_KEY_CURRENT_USER_MAIN_PHOTO_URL, userItem.baseUserInfo.mainPhotoUrl)
		editor.putString(PREF_KEY_CURRENT_USER_ID, userItem.baseUserInfo.userId)
		editor.putString(PREF_KEY_CURRENT_USER_ABOUT_TEXT, userItem.aboutText)

		val photosList = mutableListOf<String>()
		for (photo in userItem.photoURLs)
			photosList.add(gson.toJson(photo))
		editor.putString(PREF_KEY_CURRENT_USER_PHOTO_URLS, photosList.toString())

		val placesToGoList = mutableListOf<String>()
		for (place in userItem.placesToGo)
			placesToGoList.add(gson.toJson(place))
		editor.putString(PREF_KEY_CURRENT_USER_PLACES_ID, placesToGoList.toString())

		editor.putInt(PREF_KEY_CURRENT_USER_P_AGE_MIN, userItem.preferredAgeRange.minAge)
		editor.putInt(PREF_KEY_CURRENT_USER_P_AGE_MAX, userItem.preferredAgeRange.maxAge)

		editor.commit()
		Log.wtf(TAG, "User successfully saved: $userItem")
	}

	fun updatePhotosField(photoList: List<PhotoItem>) {

		val editor = prefs.edit()
		val photosList = mutableListOf<String>()
		for (photo in photoList)
			photosList.add(gson.toJson(photo))
		editor.putString(PREF_KEY_CURRENT_USER_PHOTO_URLS, photosList.toString())

		editor.apply()
	}

}