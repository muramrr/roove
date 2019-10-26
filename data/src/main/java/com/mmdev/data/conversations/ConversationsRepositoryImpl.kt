package com.mmdev.data.conversations

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

/* Created by A on 26.10.2019.*/

/**
 * This is the documentation block about the class
 */

class ConversationsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore):
		ConversationsRepository{


	override fun createConversation(): Completable {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun deleteConversation(conversationId: String): Completable {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getConversationsList(): Observable<List<ConversationItem>> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}


}