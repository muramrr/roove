package com.mmdev.business.auth.usecase

import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.core.usecase.CompletableWithParamUseCase
import com.mmdev.business.user.model.UserItem


class SignUpUseCase(private val repository: AuthRepository) :
		CompletableWithParamUseCase<UserItem> {

    override fun execute(t: UserItem) = repository.registerUser(t)
}