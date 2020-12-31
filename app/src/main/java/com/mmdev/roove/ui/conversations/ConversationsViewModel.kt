/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.conversations

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.conversations.ConversationsRepository
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject

class ConversationsViewModel @Inject constructor(
	private val repo: ConversationsRepository
): BaseViewModel() {



	private val deleteConversationStatus: MutableLiveData<Boolean> = MutableLiveData()

	val conversationsList: MutableLiveData<MutableList<ConversationItem>> = MutableLiveData()
	init {
		conversationsList.value = mutableListOf()
	}

	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()


	fun deleteConversation(conversationItem: ConversationItem){
		disposables.add(repo.deleteConversation(conversationItem)
            .observeOn(mainThread())
            .subscribe(
	            { deleteConversationStatus.value = true },
	            {
		            deleteConversationStatus.value = false
		            error.value = MyError(ErrorType.DELETING, it)
	            }
            )
		)
	}


	fun loadConversationsList(){
		disposables.add(repo.getConversationsList()
            .observeOn(mainThread())
            .subscribe(
	            {
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
		disposables.add(repo.getMoreConversationsList()
            .observeOn(mainThread())
            .subscribe(
	            {
		            if (it.isNotEmpty()) {
		            	conversationsList.value!!.addAll(it)
			            conversationsList.value = conversationsList.value
		            }
	            },
	            { error.value = MyError(ErrorType.LOADING, it) }
            )
		)
	}

	fun getDeleteConversationStatus() = deleteConversationStatus
	
}