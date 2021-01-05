/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

package com.mmdev.data.repository.pairs

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.firebase.executeAndDeserializeSingle
import com.mmdev.domain.PaginationDirection
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.pairs.PairsRepository
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * [PairsRepository] implementation
 */

class PairsRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore
): BaseRepository(), PairsRepository {
	
	companion object {
		private const val USERS_COLLECTION = "users"
	}
	
	private fun matchesQuery(user: UserItem): Query = fs.collection(USERS_COLLECTION)
		.document(user.baseUserInfo.userId)
		.collection(USER_MATCHED_COLLECTION)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
		.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
		.limit(20)
		//.startAfter(cursorPosition)
	
	override fun getPairs(
		user: UserItem,
		matchedUserId: String,
		direction: PaginationDirection
	): Single<List<MatchedUserItem>> =
		matchesQuery(user).executeAndDeserializeSingle(MatchedUserItem::class.java)
	
	
}