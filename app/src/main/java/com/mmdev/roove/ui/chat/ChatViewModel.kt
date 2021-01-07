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

package com.mmdev.roove.ui.chat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.mmdev.domain.PaginationDirection
import com.mmdev.domain.PaginationDirection.*
import com.mmdev.domain.chat.ChatRepository
import com.mmdev.domain.chat.MessageItem
import com.mmdev.domain.conversations.ConversationItem
import com.mmdev.domain.user.data.BaseUserInfo
import com.mmdev.roove.core.log.logDebug
import com.mmdev.roove.core.log.logInfo
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError


/**
 * [chatIsEmpty] used to mark conversation started or not to move partner out of pairs section
 */

class ChatViewModel @ViewModelInject constructor(
	private val repo: ChatRepository
): BaseViewModel() {
	
	val newMessage = MutableLiveData<MessageItem>()
	val initMessages = MutableLiveData<List<MessageItem>>()
	val nextMessages = MutableLiveData<List<MessageItem>>()
	
	val showLoading = MutableLiveData<Boolean>()
	val chatIsEmpty = MutableLiveData<Boolean>()

	//ui bind values
	val partnerName = MutableLiveData<String>("")
	val partnerPhoto = MutableLiveData<String>("")
	val isPartnerOnline = MutableLiveData<Boolean>(false)


	fun loadMessages(
		conversation: ConversationItem,
		lastMessage: MessageItem,
		direction: PaginationDirection
	) {
		disposables.add(repo.loadMessages(conversation, lastMessage, direction)
            .observeOn(mainThread())
            .subscribe(
				{
					chatIsEmpty.postValue(it.isEmpty())
					when (direction) {
						INITIAL -> initMessages.postValue(it)
						NEXT -> nextMessages.postValue(it)
						else -> {}
					}
					logInfo(TAG, "initial loaded messages: ${it.size}")
				},
				{ error.value = MyError(ErrorType.LOADING, it) }
            )
		)

	}

	fun observeNewMessages(conversation: ConversationItem) {
		disposables.add(repo.observeNewMessages(MainActivity.currentUser!!, conversation)
            .observeOn(mainThread())
            .subscribe(
				{ message ->
                    newMessage.postValue(message)
					chatIsEmpty.postValue(false)
					logDebug(TAG, "last received message: ${message.text}")
				},
				{ error.value = MyError(ErrorType.RECEIVING, it) }
            )
		)
	}

	//fun observePartnerOnline(conversationId: String){
	//	disposables.add(repo.observePartnerOnline(conversationId)
    //        .observeOn(mainThread())
    //        .subscribe(
	//			{ if (isPartnerOnline.value != it) isPartnerOnline.value = it },
	//			{ error.value = MyError(ErrorType.RECEIVING, it) }
    //        )
	//	)
	//}

	fun sendMessage(messageItem: MessageItem){
		disposables.add(repo.sendMessage(messageItem, chatIsEmpty.value!!)
            .observeOn(mainThread())
            .subscribe(
				{ chatIsEmpty.postValue(false) },
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
	            repo.sendMessage(photoMessage, chatIsEmpty.value!!)
            }
            .observeOn(mainThread())
            .subscribe(
				{ chatIsEmpty.postValue(false) },
				{ error.value = MyError(ErrorType.SENDING, it) }
			)
		)
	}
	
}