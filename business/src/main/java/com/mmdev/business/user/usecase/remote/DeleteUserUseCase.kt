package com.mmdev.business.user.usecase.remote

import com.mmdev.business.user.repository.UserRepository

/* Created by A on 10.11.2019.*/

/**
 * This is the documentation block about the class
 */

class DeleteUserUseCase (private val repository: UserRepository.RemoteUserRepository) {

	fun execute(t: String) = repository.deleteUser(t)

}