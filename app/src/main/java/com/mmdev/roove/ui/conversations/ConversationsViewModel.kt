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

package com.mmdev.roove.ui.conversations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.conversations.ConversationsRepository
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError

class ConversationsViewModel @ViewModelInject constructor(
	private val repo: ConversationsRepository
): BaseViewModel() {
	
	private val deleteConversationStatus: MutableLiveData<Boolean> = MutableLiveData()

	val conversationsList = MutableLiveData<List<ConversationItem>>()

	val showTextHelper = MutableLiveData<Boolean>()

	init {
		loadConversationsList(0)
	}

	fun deleteConversation(conversationItem: ConversationItem){
		disposables.add(repo.deleteConversation(MainActivity.currentUser!!, conversationItem)
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


	fun loadConversationsList(cursor: Int){
		disposables.add(repo.getConversations(MainActivity.currentUser!!, cursor)
            .observeOn(mainThread())
            .subscribe(
	            { conversations ->
		            conversationsList.postValue(conversations)
		            showTextHelper.postValue(conversations.isEmpty())
	            },
	            { error.value = MyError(ErrorType.LOADING, it) }
            )
		)
	}

	fun getDeleteConversationStatus() = deleteConversationStatus
	
}