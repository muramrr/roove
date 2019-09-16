package com.mmdev.domain.auth.usecase

import com.mmdev.domain.auth.repository.AuthRepository
import com.mmdev.domain.core.CompletableWithParamUseCase
import com.mmdev.domain.core.model.User


class SignUpUseCase(private val repository: AuthRepository) : CompletableWithParamUseCase<User> {

    override fun execute(t: User) = repository.signUp(t)
}