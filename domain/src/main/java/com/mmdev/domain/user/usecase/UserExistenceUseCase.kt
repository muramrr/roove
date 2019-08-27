package com.mmdev.domain.user.usecase

import com.mmdev.domain.user.model.User
import com.mmdev.domain.user.repository.AuthRepository
import io.reactivex.Single

/* Created by A on 27.08.2019.*/

/**
 * This is the documentation block about the class
 */

class UserExistenceUseCase(private val repository: AuthRepository){

	fun execute(t: String):Single<User> = repository.handleUserExistence(t)


}