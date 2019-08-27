package com.mmdev.domain.user.usecase

import com.mmdev.domain.core.SingleWithParamUseCase
import com.mmdev.domain.user.model.User
import com.mmdev.domain.user.repository.AuthRepository


//TODO: optimize for facebook
class SignUpUseCase(private val repository: AuthRepository) : SingleWithParamUseCase<User> {

    override fun execute(t: User) = repository.signup(t)
}