/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.data.core

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.data.repository.user.UserWrapper


/**
 * This is the documentation block about the class
 */

open class BaseRepositoryImpl constructor(private val firestore: FirebaseFirestore,
                                          private val userWrapper: UserWrapper
): Reinitable {

	protected var currentUser = userWrapper.getUser()

	protected lateinit var currentUserId: String
	protected lateinit var currentUserDocRef: DocumentReference
	init {
		if (currentUser.baseUserInfo.city.isNotEmpty() &&
				currentUser.baseUserInfo.gender.isNotEmpty() &&
				currentUser.baseUserInfo.userId.isNotEmpty()) {

			currentUserDocRef = fillUserGeneralRef(currentUser.baseUserInfo)

			currentUserId = currentUser.baseUserInfo.userId
		}

	}


	override fun reInit() {
		if (currentUser != userWrapper.getInMemoryUser()) {
			currentUser = userWrapper.getUser()

			currentUserDocRef = fillUserGeneralRef(currentUser.baseUserInfo)

			currentUserId = currentUser.baseUserInfo.userId
		}
	}

	protected fun fillUserGeneralRef (baseUserInfo: BaseUserInfo): DocumentReference {
		return firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(baseUserInfo.city)
			.collection(baseUserInfo.gender)
			.document(baseUserInfo.userId)
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

}