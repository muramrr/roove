/*
 * Created by Andrii Kovalchuk on 26.11.19 20:29
 * Copyright (c) 2019. All rights reserved.
 * Last modified 26.11.19 18:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions.conversations

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.conversations.usecase.CreateConversationUseCase
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class ConversationsViewModel @Inject constructor(private val createUC: CreateConversationUseCase,
                                                 private val deleteUC: DeleteConversationUseCase,
                                                 private val getUC: GetConversationsListUseCase):
		ViewModel(){


	private val createdConversationItem: MutableLiveData<ConversationItem> = MutableLiveData()

	private val deleteConversationStatus: MutableLiveData<Boolean> = MutableLiveData()

	private val conversationsList: MutableLiveData<List<ConversationItem>> = MutableLiveData()


	private val disposables = CompositeDisposable()


	companion object {
		private const val TAG = "mylogs"
	}


	fun createConversation(partnerCardItem: CardItem){
		disposables.add(createConversationExecution(partnerCardItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                       },{

            }))

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
                           Log.wtf(TAG, "conversations to show: ${it.size}")
                           conversationsList.value = it
                       },
                       {
                           Log.wtf(TAG, "loadConversList error + $it")
                       }))
	}

	fun getConversationsList() = conversationsList
	fun getDeleteConversationStatus() = deleteConversationStatus
	fun getCreatedConversationItem() = createdConversationItem

	fun createConversationExecution(partnerCardItem: CardItem) =
		createUC.execute(partnerCardItem)

	private fun deleteConversationExecution(conversationItem: ConversationItem) =
		deleteUC.execute(conversationItem)

	private fun getConversationsListExecution() = getUC.execute()



	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}

}