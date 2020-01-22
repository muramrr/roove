/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 17:14
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
import com.mmdev.roove.ui.core.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class ConversationsViewModel @Inject constructor(private val deleteUC: DeleteConversationUseCase,
                                                 private val getUC: GetConversationsListUseCase):
		BaseViewModel(){




	private val deleteConversationStatus: MutableLiveData<Boolean> = MutableLiveData()

	private val conversationsList: MutableLiveData<List<ConversationItem>> = MutableLiveData()


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
		                       conversationsList.value = it
		                       showTextHelper.value = false
	                       }
	                       else showTextHelper.value = true
                           Log.wtf(TAG, "conversations to show: ${it.size}")
                       },
                       {
                           Log.wtf(TAG, "load convers list error: $it")
                       }))
	}

	fun getConversationsList() = conversationsList

	fun getDeleteConversationStatus() = deleteConversationStatus


	private fun deleteConversationExecution(conversationItem: ConversationItem) =
		deleteUC.execute(conversationItem)

	private fun getConversationsListExecution() = getUC.execute()
}