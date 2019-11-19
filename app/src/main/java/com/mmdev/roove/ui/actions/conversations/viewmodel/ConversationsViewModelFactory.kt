/*
 * Created by Andrii Kovalchuk on 26.10.19 14:19
 * Copyright (c) 2019. All rights reserved.
 * Last modified 14.11.19 17:43
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions.conversations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.conversations.usecase.CreateConversationUseCase
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import javax.inject.Inject
import javax.inject.Singleton

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