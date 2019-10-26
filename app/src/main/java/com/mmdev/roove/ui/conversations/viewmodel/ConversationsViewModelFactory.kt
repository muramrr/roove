package com.mmdev.roove.ui.conversations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.conversations.usecase.CreateConversationUseCase
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import javax.inject.Inject
import javax.inject.Singleton

/* Created by A on 26.10.2019.*/

/**
 * This is the documentation block about the class
 */

@Suppress("UNCHECKED_CAST")
@Singleton
class ConversationsViewModelFactory @Inject constructor(private val createUC: CreateConversationUseCase,
                                                        private val deleteUC: DeleteConversationUseCase,
                                                        private val getUC: GetConversationsListUseCase):
		ViewModelProvider.Factory {


	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(ConversationsViewModel::class.java)) {
			return ConversationsViewModel(createUC, deleteUC, getUC) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}


}