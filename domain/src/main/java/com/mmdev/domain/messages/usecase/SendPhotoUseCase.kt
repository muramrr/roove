package com.mmdev.domain.messages.usecase

import com.mmdev.domain.messages.repository.ChatRepository

/* Created by A on 26.08.2019.*/

class SendPhotoUseCase (private val repository: ChatRepository){

	fun execute(t: String) = repository.sendPhoto(t)

}

