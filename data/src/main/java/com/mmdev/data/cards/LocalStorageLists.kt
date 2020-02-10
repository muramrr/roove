/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.02.20 17:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.cards

import com.google.gson.Gson
import com.ironz.binaryprefs.Preferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class LocalStorageLists @Inject constructor(private val prefs: Preferences) {

	private val gson = Gson()
	private val editor = prefs.edit()

	companion object{
		private const val PREF_KEY_LIKED_USERS_LIST = "liked"
		private const val PREF_KEY_MATCHED_USERS_LIST = "matched"
		private const val PREF_KEY_SKIPPED_USERS_LIST = "skipped"


		private const val TAG = "mylogs_LocalStorage"
	}


	fun getLikedList(): List<String> {
		val returnLikedList = ArrayList<String>()
		prefs.getStringSet(PREF_KEY_LIKED_USERS_LIST, setOf())?.let { returnLikedList.addAll(it)}
		return returnLikedList
	}

	fun saveLikedList(likedList: List<String>) {
		editor.putStringSet(PREF_KEY_LIKED_USERS_LIST, likedList.toSet())
		editor.commit()
	}


	fun getMatchedList(): List<String>{
		val returnMatchedList = ArrayList<String>()
		prefs.getStringSet(PREF_KEY_MATCHED_USERS_LIST, setOf())?.let { returnMatchedList.addAll(it)}
		return returnMatchedList
	}

	fun saveMatchedList(matchedList: List<String>) {
		editor.putStringSet(PREF_KEY_MATCHED_USERS_LIST, matchedList.toSet())
		editor.commit()
	}


	fun getSkippedList(): List<String> {
		val returnSkippedList = ArrayList<String>()
		prefs.getStringSet(PREF_KEY_SKIPPED_USERS_LIST, setOf())?.let { returnSkippedList.addAll(it)}
		return returnSkippedList
	}

	fun saveSkippedList(skippedList: List<String>) {
		editor.putStringSet(PREF_KEY_SKIPPED_USERS_LIST, skippedList.toSet())
		editor.commit()
	}


}