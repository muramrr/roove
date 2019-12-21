/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 19.12.19 21:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.chat.usecase.GetConversationWithPartnerUseCase
import com.mmdev.business.chat.usecase.GetMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.SendPhotoUseCase
import com.mmdev.business.conversations.ConversationItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ChatViewModel @Inject constructor(private val getConversationUC: GetConversationWithPartnerUseCase,
                                        private val getMessagesUC: GetMessagesUseCase,
                                        private val sendMessageUC: SendMessageUseCase,
                                        private val sendPhotoUC: SendPhotoUseCase) : ViewModel() {



	private var emptyChat = false
	private val messagesList: MutableLiveData<List<MessageItem>> = MutableLiveData()
	val showLoading: MutableLiveData<Boolean> = MutableLiveData()

	private val disposables = CompositeDisposable()


	companion object {
		private const val TAG = "mylogs"
	}


	fun startListenToEmptyChat(partnerId: String){
		disposables.add(getConversationExecution(partnerId)
            .flatMapObservable { getMessagesExecution(it) }
            .doOnSubscribe { showLoading.value = true }
            .doOnNext { showLoading.value = false }
            .doFinally { showLoading.value = false }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if(it.isNotEmpty()) {
		                       messagesList.value = it
		                       emptyChat = false
	                       }
	                       else emptyChat = true
	                       Log.wtf(TAG, "empty chat messages to show: ${it.size}")
	                       Log.wtf("mylogs", "is empty sent? + $emptyChat")
                       },
                       {
	                       Log.wtf(TAG, "get messages empty chat error: $it")
                       }))
	}

	fun loadMessages(conversation: ConversationItem){
		disposables.add(getMessagesExecution(conversation)
            .doOnSubscribe { showLoading.value = true }
            .doOnNext { showLoading.value = false }
            .doFinally { showLoading.value = false }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if(it.isNotEmpty()) {
		                       messagesList.value = it
		                       emptyChat = false
	                       }
	                       else emptyChat = true
	                       Log.wtf(TAG, "messages to show: ${it.size}")
	                       Log.wtf("mylogs", "is empty sent? + $emptyChat")
                       },
                       {
	                       Log.wtf(TAG, "get messages error: $it")
                       }))
	}

	fun sendMessage(messageItem: MessageItem){
		disposables.add(sendMessageExecution(messageItem, emptyChat)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.wtf(TAG, "Message sent fragment_chat") },
                       { Log.wtf(TAG, "can't send message fragment_chat, $emptyChat") }))
	}

	fun sendPhoto(photoUri: String, sender: BaseUserInfo){
		disposables.add(sendPhotoExecution(photoUri)
            .flatMapCompletable {
	            sendMessageExecution(MessageItem(sender, photoAttachementItem = it), emptyChat)
            }
            .doOnSubscribe { showLoading.value = true }
            .doOnComplete { showLoading.value = false }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.wtf(TAG, "Photo sent") },
                       { Log.wtf(TAG, "Sending photo error: $it") }))
	}

	fun getMessagesList() = messagesList


	private fun getConversationExecution(partnerId: String) = getConversationUC.execute(partnerId)

	private fun getMessagesExecution(conversation: ConversationItem) = getMessagesUC.execute(conversation)

	private fun sendMessageExecution(messageItem: MessageItem, emptyChat: Boolean? = false) = sendMessageUC.execute(messageItem, emptyChat)

	private fun sendPhotoExecution(photoUri: String) = sendPhotoUC.execute(photoUri)



	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}

}