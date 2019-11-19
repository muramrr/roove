/*
 * Created by Andrii Kovalchuk on 10.07.19 20:28
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.11.19 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.usecase.GetMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.SendPhotoUseCase
import com.mmdev.business.chat.usecase.SetConversationUseCase

class ChatViewModel(private val getMessagesUC: GetMessagesUseCase,
                    private val sendMessageUC: SendMessageUseCase,
                    private val sendPhotoUC: SendPhotoUseCase,
                    private val setConversationUC: SetConversationUseCase) : ViewModel() {



	fun getMessages(conversationId: String) = getMessagesUC.execute(conversationId)

	fun sendMessage(messageItem: MessageItem) = sendMessageUC.execute(messageItem)

	fun sendPhoto(photoUri: String) = sendPhotoUC.execute(photoUri)

	fun setConversation(conversationId: String) = setConversationUC.execute(conversationId)


}