/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.03.20 19:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.conversations

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import com.mmdev.business.conversations.usecase.GetMoreConversationsListUseCase
import com.mmdev.roove.ui.common.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class ConversationsViewModel @Inject
constructor(private val deleteUC: DeleteConversationUseCase,
            private val getConversationsUC: GetConversationsListUseCase,
            private val getMoreConversationsUC: GetMoreConversationsListUseCase): BaseViewModel(){




	private val deleteConversationStatus: MutableLiveData<Boolean> = MutableLiveData()

	val conversationsList: MutableLiveData<MutableList<ConversationItem>> = MutableLiveData()
	init {
		conversationsList.value = mutableListOf()
	}

	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()


	fun deleteConversation(conversationItem: ConversationItem){
		disposables.add(deleteConversationExecution(conversationItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       Log.wtf(TAG, "conversation ${conversationItem.conversationId} deleted")
	                       deleteConversationStatus.value = true
                       },
                       {
	                       Log.wtf(TAG, "conversation is not deleted, error = $it")
	                       deleteConversationStatus.value = false
                       }))
	}


	fun loadConversationsList(){
		disposables.add(getConversationsListExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if (it.isNotEmpty()) {
		                       conversationsList.value = it.toMutableList()
		                       showTextHelper.value = false
	                       }
                           Log.wtf(TAG, "first loaded conversations: ${it.size}")
                       },
                       {
                           Log.wtf(TAG, "load convers list error: $it")
	                       showTextHelper.value = true
                       }))
	}

	fun loadMoreConversations(){
		disposables.add(getMoreConversationsListExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           if (it.isNotEmpty()) {
	                           conversationsList.value!!.addAll(it)
	                           conversationsList.value = conversationsList.value
                           }
                           Log.wtf(TAG, "loaded more conversations: ${it.size}")
                       },
                       {
                           Log.wtf(TAG, "load convers list error: $it")
                       }))
	}

	fun getDeleteConversationStatus() = deleteConversationStatus


	private fun deleteConversationExecution(conversationItem: ConversationItem) =
		deleteUC.execute(conversationItem)
	private fun getConversationsListExecution() =
		getConversationsUC.execute()
	private fun getMoreConversationsListExecution() =
		getMoreConversationsUC.execute()
}