package com.mmdev.domain.auth.usecase

import com.mmdev.domain.auth.repository.AuthRepository

/* Created by A on 01.09.2019.*/

/**
 * This is the documentation block about the class
 */

class LogOutUseCase (private val repository: AuthRepository) {
	fun execute() = repository.logOut()
}