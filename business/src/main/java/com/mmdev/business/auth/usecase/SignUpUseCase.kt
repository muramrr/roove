package com.mmdev.business.auth.usecase

import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.core.usecase.CompletableWithParamUseCase
import com.mmdev.business.user.model.User


class SignUpUseCase(private val repository: AuthRepository) :
		CompletableWithParamUseCase<User> {

    override fun execute(t: User) = repository.registerUser(t)
}