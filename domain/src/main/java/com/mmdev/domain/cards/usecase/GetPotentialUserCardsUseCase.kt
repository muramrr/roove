package com.mmdev.domain.cards.usecase

import com.mmdev.domain.cards.repository.CardsRepository

/* Created by A on 17.09.2019.*/

/**
 * This is the documentation block about the class
 */

class GetPotentialUserCardsUseCase(private val repository: CardsRepository) {

	fun execute() = repository.getPotentialUserCards()
}