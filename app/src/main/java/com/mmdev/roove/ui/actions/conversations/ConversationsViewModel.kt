/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 19.12.19 21:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions.conversations

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class ConversationsViewModel @Inject constructor(private val deleteUC: DeleteConversationUseCase,
                                                 private val getUC: GetConversationsListUseCase):
		ViewModel(){




	private val deleteConversationStatus: MutableLiveData<Boolean> = MutableLiveData()

	private val conversationsList: MutableLiveData<List<ConversationItem>> = MutableLiveData()


	private val disposables = CompositeDisposable()


	companion object {
		private const val TAG = "mylogs"
	}


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
	                       if (it.isNotEmpty()) conversationsList.value = it
                           Log.wtf(TAG, "conversations to show: ${it.size}")
                       },
                       {
                           Log.wtf(TAG, "loadConvers viewmodel error + $it")
                       }))
	}

	fun getConversationsList() = conversationsList

	fun getDeleteConversationStatus() = deleteConversationStatus


	private fun deleteConversationExecution(conversationItem: ConversationItem) =
		deleteUC.execute(conversationItem)

	private fun getConversationsListExecution() = getUC.execute()



	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}

}