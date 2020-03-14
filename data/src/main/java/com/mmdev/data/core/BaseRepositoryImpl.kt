/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.03.20 17:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.core

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.data.user.UserWrapper


/**
 * This is the documentation block about the class
 */

open class BaseRepositoryImpl constructor(private val firestore: FirebaseFirestore,
                                          private val userWrapper: UserWrapper) {

	internal var currentUser = userWrapper.getUser()
	internal var currentUserId: String = currentUser.baseUserInfo.userId

	internal var currentUserDocRef: DocumentReference = firestore.collection(USERS_COLLECTION_REFERENCE)
		.document(currentUser.baseUserInfo.city)
		.collection(currentUser.baseUserInfo.gender)
		.document(currentUser.baseUserInfo.userId)

	internal var initialConversationsQuery: Query = currentUserDocRef
		.collection(CONVERSATIONS_COLLECTION_REFERENCE)
		.orderBy(CONVERSATION_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, true)
		.limit(20)


	internal lateinit var paginateLastConversationLoaded: DocumentSnapshot
	internal lateinit var paginateConversationsQuery: Query


	internal val TAG = "mylogs_" + javaClass.simpleName

	companion object {
		// firestore users references
		const val USERS_COLLECTION_REFERENCE = "users"
		const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
		// firestore conversations reference
		const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		const val CONVERSATION_TIMESTAMP_FIELD = "lastMessageTimestamp"
		const val CONVERSATION_DELETED_FIELD = "conversationDeleted"

		const val USER_ID_FIELD = "userId"
	}
}