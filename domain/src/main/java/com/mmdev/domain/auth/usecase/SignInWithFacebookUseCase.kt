package com.mmdev.domain.auth.usecase

import com.mmdev.domain.auth.model.User
import com.mmdev.domain.auth.repository.AuthRepository
import com.mmdev.domain.core.SingleWithParamUseCase
import io.reactivex.Single

/* Created by A on 29.08.2019.*/

/**
 * This is the documentation block about the class
 */

class SignInWithFacebookUseCase (private val repository: AuthRepository):
		SingleWithParamUseCase<String, User> {

	override fun execute(t: String): Single<User> = repository.signInWithFacebook(t)

}