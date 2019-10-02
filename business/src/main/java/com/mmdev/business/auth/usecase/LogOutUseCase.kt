package com.mmdev.business.auth.usecase

import com.mmdev.business.auth.repository.AuthRepository

/* Created by A on 01.09.2019.*/

/**
 * This is the documentation block about the class
 */

class LogOutUseCase (private val repository: AuthRepository) {
	fun execute() = repository.logOut()
}