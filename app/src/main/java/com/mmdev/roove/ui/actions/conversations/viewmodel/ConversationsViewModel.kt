/*
 * Created by Andrii Kovalchuk on 23.11.19 19:40
 * Copyright (c) 2019. All rights reserved.
 * Last modified 23.11.19 18:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions.conversations.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.conversations.usecase.CreateConversationUseCase
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class ConversationsViewModel @Inject constructor(private val createUC: CreateConversationUseCase,
                                                 private val deleteUC: DeleteConversationUseCase,
                                                 private val getUC: GetConversationsListUseCase): ViewModel(){


	fun createConversation(partnerCardItem: CardItem) = createUC.execute(partnerCardItem)

	fun deleteConversation(conversationItem: ConversationItem) = deleteUC.execute(conversationItem)

	fun getConversationsList() = getUC.execute()


}