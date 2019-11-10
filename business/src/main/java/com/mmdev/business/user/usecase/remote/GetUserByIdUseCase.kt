package com.mmdev.business.user.usecase.remote

import com.mmdev.business.user.repository.UserRepository

/* Created by A on 10.11.2019.*/

/**
 * This is the documentation block about the class
 */

class GetUserByIdUseCase (private val repository: UserRepository.RemoteUserRepository) {

	fun execute(t: String) = repository.getUserById(t)

}