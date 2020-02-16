/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.02.20 16:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mooveit.library.Fakeit
import kotlin.random.Random

object UtilityManager {

	private val db: FirebaseFirestore

	init {
		Fakeit.initWithLocale("ru")
		db = FirebaseFirestore.getInstance()
	}

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
			db.collection("users")
				.document(userItem.baseUserInfo.city)
				.collection(userItem.baseUserInfo.gender)
				.document(userItem.baseUserInfo.userId)
				.set(userItem)
	}
}