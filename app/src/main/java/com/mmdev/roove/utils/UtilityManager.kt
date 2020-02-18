/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.02.20 18:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mooveit.library.Fakeit
import kotlin.random.Random

object UtilityManager {

	private val db: FirebaseFirestore

	init {
		Fakeit.init()
		db = FirebaseFirestore.getInstance()
	}

	private const val USERS_COLLECTION_REFERENCE = "users"
	private const val USER_LIKED_COLLECTION_REFERENCE = "liked"
	private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
	private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
	private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"

	private fun createFakeUser(city: String = "nsk", gender: String = "female")=
		UserItem(BaseUserInfo(Fakeit.name().firstName(),
		                      Random.nextInt(18, 22),
		                      city,
		                      gender,
		                      "male",
		                      "https://graph.facebook.com/2175470722496419/picture?height=500",
		                      randomUid()),
		         mutableListOf(),
		         mutableListOf())


	private fun randomUid(): String {
		val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
		return (1..10)
			.map { Random.nextInt(0, charPool.size) }
			.map(charPool::get)
			.joinToString("")
	}

	fun createFakeUsersOnRemote(userItem: UserItem = createFakeUser()){
		for (i in 0 until 500)
			db.collection(USERS_COLLECTION_REFERENCE)
				.document(userItem.baseUserInfo.city)
				.collection(userItem.baseUserInfo.gender)
				.document(userItem.baseUserInfo.userId)
				.set(userItem)
	}

	fun generateLocalMatchesList(matchItem: MatchedUserItem = MatchedUserItem(createFakeUser())): List<MatchedUserItem> {
		val matchedList = mutableListOf<MatchedUserItem>()
		for (i in 0 until 20)
			matchedList.add(matchItem)
		return  matchedList
	}
}