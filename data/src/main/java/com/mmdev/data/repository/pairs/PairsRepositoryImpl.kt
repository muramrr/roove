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

import android.util.ArrayMap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.firebase.executeAndDeserializeSingle
import com.mmdev.domain.PaginationDirection
import com.mmdev.domain.PaginationDirection.*
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.pairs.PairsRepository
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

/**
 * [PairsRepository] implementation
 */

class PairsRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore
): BaseRepository(), PairsRepository {
	
	
	//init {
	//	for (i in 0..159) {
	//		val matchedUserItem = UtilityManager.generateMatch(i = i)
	//		fs.collection(USERS_COLLECTION)
	//			.document("v2tqQttLfdT21tNdQDJIfbjiVYn1")
	//			.collection(USER_MATCHED_COLLECTION)
	//			.document(i.toString())
	//			.set(matchedUserItem)
	//	}
	//}
	
	
	private val pages = ArrayMap<Int, Query>()
	
	private fun matchesQuery(user: UserItem): Query = fs.collection(USERS_COLLECTION)
		.document(user.baseUserInfo.userId)
		.collection(USER_MATCHED_COLLECTION)
		.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
	
	
	override fun getPairs(
		user: UserItem,
		conversationTimestamp: Date,
		page: Int,
		direction: PaginationDirection
	): Single<List<MatchedUserItem>> = when(direction) {
		
		INITIAL -> matchesQuery(user).limit(10).also { pages[0] = it }
		
		NEXT -> {
			if (pages.containsKey(page)) {
				pages[page]
			}
			else {
				matchesQuery(user)
					.startAfter(conversationTimestamp)
					.limit(10)
					.also {
						pages[page] = it
					}
			}
			
		}
		
		PREVIOUS -> pages[page]
		
	}!!.executeAndDeserializeSingle(MatchedUserItem::class.java)
	
	
}