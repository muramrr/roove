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

package com.mmdev.data.repository.conversations

import android.util.ArrayMap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.firebase.asSingle
import com.mmdev.data.core.firebase.executeAndDeserializeSingle
import com.mmdev.domain.PaginationDirection
import com.mmdev.domain.PaginationDirection.*
import com.mmdev.domain.conversations.ConversationItem
import com.mmdev.domain.conversations.ConversationsRepository
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3
import java.util.*
import javax.inject.Inject

/**
 * [ConversationsRepository] implementation
 */

class ConversationsRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore
): BaseRepository(), ConversationsRepository {
	
	
	
	private val pages = ArrayMap<Int, Query>()
		
	
	private fun conversationsQuery(user: UserItem): Query = fs.collection(USERS_COLLECTION)
		.document(user.baseUserInfo.userId)
		.collection(CONVERSATIONS_COLLECTION)
		.orderBy(CONVERSATION_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, true)
		

	override fun deleteConversation(user: UserItem, conversation: ConversationItem): Single<Unit> =
		Single.zip(
			deleteFromPartner(user, conversation),
			deleteFromMySelf(user, conversation),
			BiFunction { t1, t2 ->
				return@BiFunction
			}
		).subscribeOn(MySchedulers.io())
	
	private fun deleteFromMySelf(user: UserItem, conversation: ConversationItem) =
		fs.collection(CONVERSATIONS_COLLECTION)
			.document(conversation.conversationId)
			//mark that conversation no need to be exists
			.set(mapOf(CONVERSATION_DELETED_FIELD to true))
			.asSingle()
			.map {
				Single.zip(
					//delete from current user conversations list
					fs.collection(USERS_COLLECTION)
						.document(user.baseUserInfo.userId)
						.collection(CONVERSATIONS_COLLECTION)
						.document(conversation.conversationId)
						.delete()
						.asSingle(),
					
					//current user delete from matched list
					fs.collection(USERS_COLLECTION)
						.document(user.baseUserInfo.userId)
						.collection(USER_MATCHED_COLLECTION)
						.document(conversation.partner.userId)
						.delete()
						.asSingle(),
					
					//add to skipped collection
					fs.collection(USERS_COLLECTION)
						.document(user.baseUserInfo.userId)
						.collection(USER_SKIPPED_COLLECTION)
						.document(conversation.partner.userId)
						.set(mapOf(USER_ID_FIELD to conversation.partner.userId))
						.asSingle(),
					
					Function3 { t1, t2, t3 ->
						return@Function3
					}
				).subscribeOn(MySchedulers.io())
			}
	
	private fun deleteFromPartner(user: UserItem, conversation: ConversationItem) =
		fs.collection(USERS_COLLECTION)
			.document(conversation.partner.userId)
			.get()
			.asSingle()
			.map {
				if (it.exists()) {
					Single.zip(
						//partner delete from matched list
						it.reference
							.collection(USER_MATCHED_COLLECTION)
							.document(user.baseUserInfo.userId)
							.delete()
							.asSingle(),
							
						//partner delete from conversations list
						it.reference
							.collection(CONVERSATIONS_COLLECTION)
							.document(conversation.conversationId)
							.delete()
							.asSingle(),
							
						//add to skipped collection
						it.reference
							.collection(USER_SKIPPED_COLLECTION)
							.document(user.baseUserInfo.userId)
							.set(mapOf(USER_ID_FIELD to user.baseUserInfo.userId))
							.asSingle(),
						
						Function3 { t1, t2, t3 ->
							return@Function3
						}
					)
					
				}
				else Single.just(Unit)
			}
	
	override fun getConversations(
		user: UserItem,
		conversationTimestamp: Date,
		page: Int,
		direction: PaginationDirection
	): Single<List<ConversationItem>> = when(direction) {
		
		INITIAL -> conversationsQuery(user).limit(20).also { pages[0] = it }
		
		NEXT -> {
			if (pages.containsKey(page)) {
				pages[page]
			}
			else {
				conversationsQuery(user)
					.startAfter(conversationTimestamp)
					.limit(20)
					.also {
						pages[page] = it
					}
			}
			
		}
		
		PREVIOUS -> pages[page]
		
	}!!.executeAndDeserializeSingle(ConversationItem::class.java)
	
}