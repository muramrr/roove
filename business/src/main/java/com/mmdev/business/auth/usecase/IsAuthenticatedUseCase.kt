package com.mmdev.business.auth.usecase

import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.core.usecase.ObservableUseCase

/* Created by A on 01.09.2019.*/

/**
 * This is the documentation block about the class
 */

class IsAuthenticatedUseCase (private val repository: AuthRepository):
		ObservableUseCase<Boolean> {

	override fun execute() = repository.isAuthenticated()

}