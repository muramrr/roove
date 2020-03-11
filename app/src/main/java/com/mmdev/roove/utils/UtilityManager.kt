/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.03.20 20:54
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.PhotoItem
import com.mmdev.business.core.UserItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mooveit.library.Fakeit
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.random.Random


object UtilityManager {

	private val db: FirebaseFirestore
	private val randomFemalePhotoUrlsList: List<String>
	private val randomMalePhotoUrlsList: List<String>
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

		randomMalePhotoUrlsList = listOf(
				"https://images.unsplash.com/photo-1575751746286-e215a17139b6?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1531599890467-0673e8859bbf?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1506134501047-b0fefdacbb04?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1534235187448-833893dfe3e0?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1551628723-952088378fd3?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1510778670743-06254c768dad?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1522318548694-a0b65c40b0c6?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1555069519-127aadedf1ee?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1565766946249-4fb344ba4a90?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500",
				"https://images.unsplash.com/photo-1559407838-3cf395241a32?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=1000&ixid=eyJhcHBfaWQiOjF9&ixlib=rb-1.2.1&q=80&w=500"
		)
	}

	private const val USERS_COLLECTION_REFERENCE = "users"
	private const val USER_LIKED_COLLECTION_REFERENCE = "liked"
	private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
	private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
	private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"

	private const val GENDER_FOR_WHICH_CREATE = "female"
	private const val CITY_WHERE_CREATE = "nnv"
	private const val USERID_FOR_WHICH_CREATE = "g5q5vvujWkXcn0uwKdE4YEgOvnp2"

	private fun createFakeUser(city: String = CITY_WHERE_CREATE,
	                           gender: String = "male",
	                           preferredGender: String = "female"): UserItem {
		val randomPhotoNum = Random.nextInt(0, 9)
		val randomPhotoUrl = randomMalePhotoUrlsList[randomPhotoNum]
		val randomPhotoList = mutableListOf<PhotoItem>()
		randomPhotoList.add(PhotoItem(fileUrl = randomPhotoUrl))
		for (i in 0 until randomPhotoNum-1) {
			val anotherRandomIndex = Random.nextInt(0, randomPhotoNum)
			val randomPhotoItem = PhotoItem(fileUrl = randomMalePhotoUrlsList[anotherRandomIndex])
			randomPhotoList.add(randomPhotoItem)
		}
		return UserItem(BaseUserInfo(name = Fakeit.name().firstName(),
		                             age = Random.nextInt(18, 22),
		                             city = city,
		                             gender = gender,
		                             preferredGender = preferredGender,
		                             mainPhotoUrl = randomPhotoUrl,
		                             userId = randomUid()),
		                photoURLs = randomPhotoList,
		                placesToGo = mutableListOf())
	}


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

	fun generateMatchesOnRemote() {

		val randomDate = generateRandomDate()
		val userItem: UserItem = createFakeUser()

		db.collection(USERS_COLLECTION_REFERENCE)
			.document(CITY_WHERE_CREATE)
			.collection(GENDER_FOR_WHICH_CREATE)
			.document(USERID_FOR_WHICH_CREATE)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(userItem.baseUserInfo.userId)
			.set(MatchedUserItem(userItem.baseUserInfo, matchedDate = randomDate))
		createFakeUserOnRemote(userItem)

	}

	fun generateConversationOnRemote() {

		val conversationId = randomUid()
		val userItem: UserItem = createFakeUser()

		db.collection(USERS_COLLECTION_REFERENCE)
			.document(CITY_WHERE_CREATE)
			.collection(GENDER_FOR_WHICH_CREATE)
			.document(USERID_FOR_WHICH_CREATE)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
			.set(ConversationItem(conversationId = conversationId,
			                      conversationStarted = true,
			                      lastMessageTimestamp = generateRandomDate(),
			                      lastMessageText = Fakeit.lorem().words(),
			                      partner = userItem.baseUserInfo))

		createFakeUserOnRemote(userItem)
	}
}