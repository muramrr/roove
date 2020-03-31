/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.03.20 15:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.core

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.data.user.UserWrapper


/**
 * This is the documentation block about the class
 */

open class BaseRepositoryImpl constructor(private val firestore: FirebaseFirestore,
                                          private val userWrapper: UserWrapper): Reinitable {

	protected var currentUser = userWrapper.getUser()

	protected lateinit var currentUserId: String
	protected lateinit var currentUserDocRef: DocumentReference
	init {
		if (currentUser.baseUserInfo.city.isNotEmpty() &&
				currentUser.baseUserInfo.gender.isNotEmpty() &&
				currentUser.baseUserInfo.userId.isNotEmpty()) {

			currentUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUser.baseUserInfo.city)
				.collection(currentUser.baseUserInfo.gender)
				.document(currentUser.baseUserInfo.userId)

			currentUserId = currentUser.baseUserInfo.userId
		}

	}


	override fun reInit() {
		if (currentUser != userWrapper.getInMemoryUser()) {
			currentUser = userWrapper.getUser()

			currentUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUser.baseUserInfo.city)
				.collection(currentUser.baseUserInfo.gender)
				.document(currentUser.baseUserInfo.userId)

			currentUserId = currentUser.baseUserInfo.userId
		}
	}

	internal val TAG = "mylogs_" + javaClass.simpleName

	companion object {
		// firestore users collection references
		const val USERS_COLLECTION_REFERENCE = "users"
		const val USERS_BASE_COLLECTION_REFERENCE = "usersBase"
		const val USER_LIKED_COLLECTION_REFERENCE = "liked"
		const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
		// firestore conversations reference
		const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		const val CONVERSATION_TIMESTAMP_FIELD = "lastMessageTimestamp"
		const val CONVERSATION_LAST_MESSAGE_TEXT_FIELD = "lastMessageText"
		const val CONVERSATION_DELETED_FIELD = "conversationDeleted"
		// Firebase Storage references
		const val GENERAL_FOLDER_STORAGE_IMG = "images"
		const val SECONDARY_FOLDER_STORAGE_IMG = "conversations"

		const val MATCHED_DATE_FIELD = "matchedDate"

		const val USER_ID_FIELD = "userId"

	}
	protected fun fillUserGeneralRef (baseUserInfo: BaseUserInfo): DocumentReference {
		return firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(baseUserInfo.city)
			.collection(baseUserInfo.gender)
			.document(baseUserInfo.userId)
	}
}