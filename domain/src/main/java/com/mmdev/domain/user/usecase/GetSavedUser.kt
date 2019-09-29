package com.mmdev.domain.user.usecase

import com.mmdev.domain.user.repository.UserRepository

/* Created by A on 29.09.2019.*/

/**
 * This is the documentation block about the class
 */

class GetSavedUser (private val repository: UserRepository) {

	fun execute() = repository.getSavedUser()
}