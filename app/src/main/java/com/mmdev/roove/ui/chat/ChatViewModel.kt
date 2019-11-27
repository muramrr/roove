/*
 * Created by Andrii Kovalchuk on 27.11.19 19:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 27.11.19 19:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.usecase.CreateConversationUseCase
import com.mmdev.business.chat.usecase.GetMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.SendPhotoUseCase
import com.mmdev.business.conversations.model.ConversationItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class ChatViewModel(private val createUC: CreateConversationUseCase,
                    private val getMessagesUC: GetMessagesUseCase,
                    private val sendMessageUC: SendMessageUseCase,
                    private val sendPhotoUC: SendPhotoUseCase) : ViewModel() {


	private val createdConversationItem: MutableLiveData<ConversationItem> = MutableLiveData()


	private val disposables = CompositeDisposable()



	companion object {
		private const val TAG = "mylogs"
	}


	fun createConversation(partnerCardItem: CardItem){
		disposables.add(createConversationExecution(partnerCardItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                       },{

                       }))

	}


	fun getCreatedConversationItem() = createdConversationItem

	fun getMessages(conversationId: String) = getMessagesUC.execute(conversationId)

	fun sendMessage(messageItem: MessageItem) = sendMessageUC.execute(messageItem)

	fun sendPhoto(photoUri: String) = sendPhotoUC.execute(photoUri)

	private fun createConversationExecution(partnerCardItem: CardItem) =
		createUC.execute(partnerCardItem)


	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}

}