/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.03.20 20:10
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.chat.usecase.LoadMessagesUseCase
import com.mmdev.business.chat.usecase.ObserveNewMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.UploadMessagePhotoUseCase
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class ChatViewModel @Inject constructor(repo: ChatRepository) :
		BaseViewModel() {


	private val loadMessagesUC = LoadMessagesUseCase(repo)
	private val observeNewMessagesUC = ObserveNewMessagesUseCase(repo)
	private val sendMessageUC = SendMessageUseCase(repo)
	private val uploadMessagePhotoUC = UploadMessagePhotoUseCase(repo)

	private val messagesList: MutableLiveData<MutableList<MessageItem>> = MutableLiveData()
	init {
		messagesList.value = mutableListOf()
	}

	private lateinit var selectedConversation: ConversationItem

	private var emptyChat = true

	val showLoading: MutableLiveData<Boolean> = MutableLiveData()


	fun loadMessages(conversation: ConversationItem) {
		selectedConversation = conversation
		disposables.add(loadMessagesExecution(conversation)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if(it.isNotEmpty()) {
		                       messagesList.value?.addAll(it)
		                       messagesList.value = messagesList.value
		                       emptyChat = false
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
		selectedConversation = conversation
		disposables.add(observeNewMessagesExecution(conversation)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       messagesList.value?.add(0, it)
	                       messagesList.value = messagesList.value
	                       emptyChat = false

	                       Log.wtf(TAG, "new message in conversation: ${it.text}")
                       },
                       {
	                       error.value = MyError(ErrorType.RECEIVING, it)
                       }
            )
		)
	}

	fun sendMessage(messageItem: MessageItem){
		disposables.add(sendMessageExecution(messageItem, emptyChat)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ /*Log.wtf(TAG, "Message sent")*/ },
                       { error.value = MyError(ErrorType.SENDING, it) })
		)
	}

	//upload photo then send it as message item
	fun sendPhoto(photoUri: String, sender: BaseUserInfo, recipient: String) {
		disposables.add(uploadPhotoExecution(photoUri)
            .flatMapCompletable {
	            val photoMessage = MessageItem(sender = sender,
	                                           recipientId = recipient,
	                                           photoItem = it,
	                                           conversationId = selectedConversation.conversationId)
	            sendMessageExecution(photoMessage, emptyChat)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ /*Log.wtf(TAG, "Photo sent")*/ },
                       { error.value = MyError(ErrorType.SENDING, it) }))
	}

	fun getMessagesList() = messagesList


	private fun loadMessagesExecution(conversation: ConversationItem) =
		loadMessagesUC.execute(conversation)
	private fun observeNewMessagesExecution(conversation: ConversationItem) =
		observeNewMessagesUC.execute(conversation)
	private fun sendMessageExecution(messageItem: MessageItem, emptyChat: Boolean? = false) =
		sendMessageUC.execute(messageItem, emptyChat)
	private fun uploadPhotoExecution(photoUri: String) =
		uploadMessagePhotoUC.execute(photoUri)
}