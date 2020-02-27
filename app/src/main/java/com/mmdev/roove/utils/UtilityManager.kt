/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.02.20 15:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.UserItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mooveit.library.Fakeit
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.random.Random


object UtilityManager {

	private val db: FirebaseFirestore
	private var randomFemalePhotoUrlsList: List<String>
	init {
		Fakeit.init()
		db = FirebaseFirestore.getInstance()
		randomFemalePhotoUrlsList = listOf(
				"https://images.unsplash.com/photo-1520419423130-2967ef45211a?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1492106087820-71f1a00d2b11?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1510649343685-d3b08f12def1?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1464863979621-258859e62245?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1464863979621-258859e62245?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1447194047554-cfe888edc98c?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1474901879171-d6f34b3a99b0?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1515017051947-c15e2934398b?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1513792859704-f49baf5c0b70?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1514846326710-096e4a8035e0?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500"
		)
	}

	private const val USERS_COLLECTION_REFERENCE = "users"
	private const val USER_LIKED_COLLECTION_REFERENCE = "liked"
	private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
	private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
	private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"

	private const val GENDER = "female"
	private const val CITY = "msk"
	private const val USERID = "L6udxztFxqadDeHvN6qXdIpZihF3"

	private fun createFakeUser(city: String = "nsk", gender: String = "female")=
		UserItem(BaseUserInfo(Fakeit.name().firstName(),
		                                                                 Random.nextInt(18, 22),
		                                                                 city,
		                                                                 gender,
		                                                                 "male",
		                                                                 randomFemalePhotoUrlsList[Random.nextInt(
				                                                                 0,
				                                                                 9)],
		                                                                 randomUid()),
		                                                    photoURLs = randomFemalePhotoUrlsList.toMutableList(),
		                                                    placesToGo = mutableListOf())


	private fun randomUid(): String {
		val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
		return (1..10)
			.map { Random.nextInt(0, charPool.size) }
			.map(charPool::get)
			.joinToString("")
	}

	private fun generateDateBetween(startInclusive: Date, endExclusive: Date): Date {
		val startMillis: Long = startInclusive.time
		val endMillis: Long = endExclusive.time
		val randomMillisSinceEpoch: Long = ThreadLocalRandom.current().nextLong(startMillis, endMillis)
		return Date(randomMillisSinceEpoch)
	}

	private fun generateRandomDate(): Date {
		val aDay: Long = TimeUnit.DAYS.toMillis(1)
		val now = Date().time
		val hundredYearsAgo = Date(now - aDay * 365 * 100)
		val tenDaysAgo = Date(now - aDay * 10)
		return generateDateBetween(hundredYearsAgo, tenDaysAgo)
	}

	fun createFakeUserOnRemote(userItem: UserItem = createFakeUser()) {
		db.collection(USERS_COLLECTION_REFERENCE)
			.document(userItem.baseUserInfo.city)
			.collection(userItem.baseUserInfo.gender)
			.document(userItem.baseUserInfo.userId)
			.set(userItem)
	}

	fun generateMatchesOnRemote(userItem: UserItem = createFakeUser()) {

		val randomDate = generateRandomDate()

		db.collection(USERS_COLLECTION_REFERENCE)
			.document(CITY)
			.collection(GENDER)
			.document(USERID)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(userItem.baseUserInfo.userId)
			.set(MatchedUserItem(userItem, matchedDate = randomDate))

	}

	fun generateConversationOnRemote(){
		val conversationId = randomUid()
		db.collection(USERS_COLLECTION_REFERENCE)
			.document(CITY)
			.collection(GENDER)
			.document(USERID)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
			.set(ConversationItem(conversationId = conversationId,
			                      conversationStarted = true,
			                      lastMessageTimestamp = generateRandomDate(),
			                      lastMessageText = Fakeit.lorem().words(),
			                      partner = createFakeUser()))


	}
}