package com.mmdev.business.auth.usecase

import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.core.usecase.SingleWithParamUseCase
import com.mmdev.business.user.model.User
import io.reactivex.Single

/* Created by A on 27.08.2019.*/

/**
 * This is the documentation block about the class
 */

class HandleUserExistenceUseCase(private val repository: AuthRepository):
		SingleWithParamUseCase<String, User> {

	override fun execute(t: String) : Single<User> = repository.handleUserExistence(t)


}