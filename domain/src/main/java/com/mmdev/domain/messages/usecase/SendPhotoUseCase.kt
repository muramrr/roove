package com.mmdev.domain.messages.usecase

import com.mmdev.domain.core.CompletableMultipleParamUseCase
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.repository.ChatRepository
import java.io.File

/* Created by A on 26.08.2019.*/

class SendPhotoUseCase (private val repository: ChatRepository) :
		CompletableMultipleParamUseCase<Message,File> {

	override fun execute(t1: Message, t2: File) = repository.sendPhoto(t1, t2)
}

