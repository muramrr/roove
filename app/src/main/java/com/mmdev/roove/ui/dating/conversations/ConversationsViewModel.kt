/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 19:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.conversations

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import com.mmdev.business.conversations.usecase.GetMoreConversationsListUseCase
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject

class ConversationsViewModel @Inject constructor(repo: ConversationsRepository): BaseViewModel() {

	private val deleteUC = DeleteConversationUseCase(repo)
	private val getConversationsUC = GetConversationsListUseCase(repo)
	private val getMoreConversationsUC = GetMoreConversationsListUseCase(repo)


	private val deleteConversationStatus: MutableLiveData<Boolean> = MutableLiveData()

	val conversationsList: MutableLiveData<MutableList<ConversationItem>> = MutableLiveData()
	init {
		conversationsList.value = mutableListOf()
	}

	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()


	fun deleteConversation(conversationItem: ConversationItem){
		disposables.add(deleteConversationExecution(conversationItem)
            .observeOn(mainThread())
            .subscribe({
	                       deleteConversationStatus.value = true
                       },
                       {
	                       deleteConversationStatus.value = false
	                       error.value = MyError(ErrorType.DELETING, it)
                       }))
	}


	fun loadConversationsList(){
		disposables.add(getConversationsListExecution()
            .observeOn(mainThread())
            .subscribe({
	                       if (it.isNotEmpty()) {
		                       conversationsList.value = it.toMutableList()
		                       showTextHelper.value = false
	                       }
	                       else showTextHelper.value = true
                       },
                       {
	                       showTextHelper.value = true
	                       error.value = MyError(ErrorType.LOADING, it)
                       }
            )
		)
	}

	fun loadMoreConversations(){
		disposables.add(getMoreConversationsListExecution()
            .observeOn(mainThread())
            .subscribe({
                           if (it.isNotEmpty()) {
	                           conversationsList.value!!.addAll(it)
	                           conversationsList.value = conversationsList.value
                           }
                       },
                       {
	                       error.value = MyError(ErrorType.LOADING, it)
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