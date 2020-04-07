/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.04.20 13:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.chat.MessageItem
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.chat.usecase.*
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject

class ChatViewModel @Inject constructor(repo: ChatRepository) :
		BaseViewModel() {


	private val loadMessagesUC = LoadMessagesUseCase(repo)
	private val loadMoreMessagesUC = LoadMoreMessagesUseCase(repo)
	private val observeNewMessagesUC = ObserveNewMessagesUseCase(repo)
	private val sendMessageUC = SendMessageUseCase(repo)
	private val uploadMessagePhotoUC = UploadMessagePhotoUseCase(repo)

	val messagesList: MutableLiveData<MutableList<MessageItem>> = MutableLiveData()
	init {
		messagesList.value = mutableListOf()
	}
	val showLoading: MutableLiveData<Boolean> = MutableLiveData()
	val newMessage: MutableLiveData<MessageItem> = MutableLiveData()

	val chatIsEmpty: MutableLiveData<Boolean> = MutableLiveData()


	fun loadMessages(conversation: ConversationItem) {
		disposables.add(loadMessagesExecution(conversation)
            .observeOn(mainThread())
            .subscribe({
	                       if(it.isNotEmpty()) {
		                       messagesList.value = it.toMutableList()
		                       chatIsEmpty.value = false
	                       }
	                       else chatIsEmpty.value = true
	                       Log.wtf(TAG, "initial loaded messages: ${it.size}")
                       },
                       {
	                       chatIsEmpty.value = true
	                       error.value = MyError(ErrorType.LOADING, it)
                       }
            )
		)

	}

	fun loadMoreMessages() {
		disposables.add(loadMoreMessagesExecution()
            .observeOn(mainThread())
            .subscribe({
                           if(it.isNotEmpty()) {
	                           messagesList.value!!.addAll(it)
	                           messagesList.value = messagesList.value
                           }
                           Log.wtf(TAG, "pagination loaded messages: ${it.size}")
                       },
                       {
                           error.value = MyError(ErrorType.LOADING, it)
                       }
            )
		)
	}

	fun observeNewMessages(conversation: ConversationItem){
		disposables.add(observeNewMessagesExecution(conversation)
            .observeOn(mainThread())
            .subscribe({
	                       newMessage.value = it
	                       messagesList.value!!.add(0, it)
	                       chatIsEmpty.value = false
	                       Log.wtf(TAG, "last received message: ${it.text}")
                       },
                       {
	                       error.value = MyError(ErrorType.RECEIVING, it)
                       }
            )
		)
	}

	fun sendMessage(messageItem: MessageItem){
		disposables.add(sendMessageExecution(messageItem, chatIsEmpty.value)
            .observeOn(mainThread())
            .subscribe({ /*Log.wtf(TAG, "Message sent")*/ },
                       { error.value = MyError(ErrorType.SENDING, it) })
		)
	}

	//upload photo then send it as message item
	fun sendPhoto(photoUri: String, conversation: ConversationItem, sender: BaseUserInfo) {
		disposables.add(uploadPhotoExecution(photoUri, conversation.conversationId)
            .flatMapCompletable {
	            val photoMessage =
		            MessageItem(sender = sender,
		                        recipientId = conversation.partner.userId,
		                        photoItem = it,
		                        conversationId = conversation.conversationId)
	            sendMessageExecution(photoMessage, chatIsEmpty.value)
            }
            .observeOn(mainThread())
            .subscribe({ /*Log.wtf(TAG, "Photo sent")*/ },
                       { error.value = MyError(ErrorType.SENDING, it) }))
	}


	private fun loadMessagesExecution(conversation: ConversationItem) =
		loadMessagesUC.execute(conversation)
	private fun loadMoreMessagesExecution() = loadMoreMessagesUC.execute()
	private fun observeNewMessagesExecution(conversation: ConversationItem) =
		observeNewMessagesUC.execute(conversation)
	private fun sendMessageExecution(messageItem: MessageItem, emptyChat: Boolean? = false) =
		sendMessageUC.execute(messageItem, emptyChat)
	private fun uploadPhotoExecution(photoUri: String, conversationId: String) =
		uploadMessagePhotoUC.execute(photoUri, conversationId)
}