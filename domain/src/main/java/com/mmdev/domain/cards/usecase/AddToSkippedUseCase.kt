package com.mmdev.domain.cards.usecase

import com.mmdev.domain.cards.repository.CardsRepository
import com.mmdev.domain.core.model.User

/* Created by A on 17.09.2019.*/

/**
 * This is the documentation block about the class
 */

class AddToSkippedUseCase (private val repository: CardsRepository)  {

	fun execute(t: User) = repository.addToSkipped(t)

}