/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.chat

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.chat.ChatRepository
import com.mmdev.business.chat.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.roove.core.log.logDebug
import com.mmdev.roove.core.log.logInfo
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject


/**
 * [chatIsEmpty] used to mark conversation started or not to move partner out of pairs section
 */

class ChatViewModel @Inject constructor(
	private val repo: ChatRepository
): BaseViewModel() {

	val messagesList = MutableLiveData<MutableList<MessageItem>>(mutableListOf())
	val showLoading = MutableLiveData<Boolean>()
	val chatIsEmpty = MutableLiveData<Boolean>()

	//ui bind values
	val partnerName = MutableLiveData<String>("")
	val partnerPhoto = MutableLiveData<String>("")
	val isPartnerOnline = MutableLiveData<Boolean>(false)


	fun loadMessages(conversation: ConversationItem) {
		disposables.add(repo.loadMessages(conversation)
            .observeOn(mainThread())
            .subscribe(
				{
					if(it.isNotEmpty()) {
					   messagesList.value = it.toMutableList()
					   chatIsEmpty.value = false
					}
					else chatIsEmpty.value = true
					logInfo(TAG, "initial loaded messages: ${it.size}")
				},
				{
					chatIsEmpty.value = true
					error.value = MyError(ErrorType.LOADING, it)
				}
            )
		)

	}

	fun loadMoreMessages() {
		disposables.add(repo.loadMoreMessages()
            .observeOn(mainThread())
            .subscribe(
				{
					if(it.isNotEmpty()) {
						messagesList.value!!.addAll(it)
						messagesList.value = messagesList.value
					}
					logDebug(TAG, "pagination loaded messages: ${it.size}")
				},
				{ error.value = MyError(ErrorType.LOADING, it) }
            )
		)
	}

	fun observeNewMessages(conversation: ConversationItem){
		disposables.add(repo.observeNewMessages(conversation)
            .observeOn(mainThread())
            .subscribe(
				{ message ->
                    messagesList.value!!.add(0, message)
					chatIsEmpty.value = false
					logDebug(TAG, "last received message: ${message.text}")
				},
				{ error.value = MyError(ErrorType.RECEIVING, it) }
            )
		)
	}

	fun observePartnerOnline(conversationId: String){
		disposables.add(repo.observePartnerOnline(conversationId)
            .observeOn(mainThread())
            .subscribe(
				{ if (isPartnerOnline.value != it) isPartnerOnline.value = it },
				{ error.value = MyError(ErrorType.RECEIVING, it) }
            )
		)
	}

	fun sendMessage(messageItem: MessageItem){
		disposables.add(repo.sendMessage(messageItem, chatIsEmpty.value)
            .observeOn(mainThread())
            .subscribe(
				{ /*Log.wtf(TAG, "Message sent")*/ },
				{ error.value = MyError(ErrorType.SENDING, it) }
			)
		)
	}

	//upload photo then send it as message item
	fun sendPhoto(photoUri: String, conversation: ConversationItem, sender: BaseUserInfo) {
		disposables.add(repo.uploadMessagePhoto(photoUri, conversation.conversationId)
            .flatMapCompletable {
	            val photoMessage = MessageItem(
		            sender = sender,
		            recipientId = conversation.partner.userId,
		            photoItem = it,
		            conversationId = conversation.conversationId
	            )
	            repo.sendMessage(photoMessage, chatIsEmpty.value)
            }
            .observeOn(mainThread())
            .subscribe(
				{ /*Log.wtf(TAG, "Photo sent")*/ },
				{ error.value = MyError(ErrorType.SENDING, it) }
			)
		)
	}
	
}