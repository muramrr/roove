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
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.user.UserItem
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.firebase.executeAndDeserializeSingle
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
	
	private fun matchesQuery(user: UserItem, cursorPosition: Int): Query = fs.collection(USERS_COLLECTION)
		.document(user.baseUserInfo.userId)
		.collection(USER_MATCHED_COLLECTION)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
		.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
		.limit(20)
		.startAfter(cursorPosition)
	
	override fun getPairs(user: UserItem, cursorPosition: Int): Single<List<MatchedUserItem>> =
		matchesQuery(user, cursorPosition)
			.executeAndDeserializeSingle(MatchedUserItem::class.java)
	
	
}