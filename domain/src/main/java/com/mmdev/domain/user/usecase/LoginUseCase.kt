package com.mmdev.domain.user.usecase


import com.mmdev.domain.core.SingleWithParamUseCase
import com.mmdev.domain.user.model.User
import com.mmdev.domain.user.repository.AuthRepository


//TODO: optimize for facebook
class LoginUseCase(private val repository: AuthRepository) : SingleWithParamUseCase<User, User> {

    override fun execute(t: User) = repository.login(t.name, t.name)
}