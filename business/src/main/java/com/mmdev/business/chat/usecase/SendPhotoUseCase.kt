package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.repository.ChatRepository

/* Created by A on 26.08.2019.*/

class SendPhotoUseCase (private val repository: ChatRepository){

	fun execute(t: String) = repository.sendPhoto(t)

}

