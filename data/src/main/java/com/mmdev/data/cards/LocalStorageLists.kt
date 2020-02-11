/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.02.20 18:28
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.cards

import com.ironz.binaryprefs.Preferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class LocalStorageLists @Inject constructor(private val prefs: Preferences) {

	private val editor = prefs.edit()

	companion object{
		private const val PREF_KEY_CACHED_USERS = "liked"

		private const val TAG = "mylogs_LocalStorage"
	}


	fun getCachedUsersList(): List<String> {
		val returnLikedList = ArrayList<String>()
		prefs.getStringSet(PREF_KEY_CACHED_USERS, setOf())?.let { returnLikedList.addAll(it)}
		return returnLikedList
	}

	fun saveChachedUsersdList(likedList: List<String>) {
		editor.putStringSet(PREF_KEY_CACHED_USERS, likedList.toSet())
		editor.commit()
	}


}