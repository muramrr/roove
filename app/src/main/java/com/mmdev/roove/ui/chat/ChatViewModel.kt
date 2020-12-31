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

package com.mmdev.roove.ui.chat

import androidx.hilt.lifecycle.ViewModelInject
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


/**
 * [chatIsEmpty] used to mark conversation started or not to move partner out of pairs section
 */

class ChatViewModel @ViewModelInject constructor(
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