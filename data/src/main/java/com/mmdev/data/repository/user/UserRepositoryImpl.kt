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

package com.mmdev.data.repository.user

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.firebase.asSingle
import com.mmdev.data.core.firebase.deleteAsCompletable
import com.mmdev.data.core.firebase.setAsCompletable
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.user.IUserRepository
import com.mmdev.domain.user.data.BaseUserInfo
import com.mmdev.domain.user.data.ReportType
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * [IUserRepository]
 * Current user related access to db to manipulate own data
 */

class UserRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore
): BaseRepository(), IUserRepository {

	companion object {
		private const val REPORTS_COLLECTION = "reports"
	}
	
	override fun deleteMatchedUser(
        user: UserItem,
        matchedUserItem: MatchedUserItem
	): Completable = Completable.concatArray(
		deleteFromMatch(
			userForWhichDelete = user.baseUserInfo,
			userWhomToDelete = matchedUserItem.baseUserInfo,
			conversationId = matchedUserItem.conversationId
		),
		
		deleteFromMatch(
			userForWhichDelete = matchedUserItem.baseUserInfo,
			userWhomToDelete = user.baseUserInfo,
			conversationId = matchedUserItem.conversationId
		)
	).subscribeOn(MySchedulers.io())
	
	private fun deleteFromMatch(
        userForWhichDelete: BaseUserInfo,
        userWhomToDelete: BaseUserInfo,
        conversationId: String
	) = Completable.concatArray(
		// delete from matches
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(USER_MATCHED_COLLECTION)
			.document(userWhomToDelete.userId)
			.deleteAsCompletable(),
			
		// delete from conversations
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(conversationId)
			.deleteAsCompletable(),
			
		// add to skipped collection
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(USER_SKIPPED_COLLECTION)
			.document(userWhomToDelete.userId)
			.setAsCompletable(mapOf(USER_ID_FIELD to userWhomToDelete.userId))
	).subscribeOn(MySchedulers.io())
	

	override fun getRequestedUserItem(baseUserInfo: BaseUserInfo): Single<UserItem> =
		fs.collection(USERS_COLLECTION)
			.document(baseUserInfo.userId)
			.get()
			.asSingle()
			.map {
				if (it.exists()) it.toObject(UserItem::class.java)
				else UserItem(BaseUserInfo("DELETED"))
			}

	
	override fun submitReport(type: ReportType, baseUserInfo: BaseUserInfo): Completable =
		fs.collection(REPORTS_COLLECTION)
			.document()
			.setAsCompletable(Report(reportType = type, reportedUser = baseUserInfo))
	
}