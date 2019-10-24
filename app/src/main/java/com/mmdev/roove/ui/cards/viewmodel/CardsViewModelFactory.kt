package com.mmdev.roove.ui.cards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.GetPotentialUserCardsUseCase
import com.mmdev.business.cards.usecase.HandlePossibleMatchUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class CardsViewModelFactory
@Inject constructor(private val addToSkipped: AddToSkippedUseCase,
                    private val getPotentialUserCards: GetPotentialUserCardsUseCase,
                    private val handlePossibleMatch: HandlePossibleMatchUseCase) :
		ViewModelProvider.Factory {

	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
			return CardsViewModel(addToSkipped, getPotentialUserCards, handlePossibleMatch) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}