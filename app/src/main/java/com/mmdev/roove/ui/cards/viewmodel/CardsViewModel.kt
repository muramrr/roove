package com.mmdev.roove.ui.cards.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.GetMatchedUsersUseCase
import com.mmdev.business.cards.usecase.GetPotentialUsersUseCase
import com.mmdev.business.cards.usecase.HandlePossibleMatchUseCase

/* Created by A on 20.07.2019.*/

/**
 * get users from firebase firestore
 * getAllUsers -> getSkipedUsers -> getLikedUsers -> getMatchedUsers -> mergeLikedSkipedMatched ->
 * -> create new list from getAllUsersCards list that does not contains mergedLikedSkipedMatched items
 * -> postValue into LiveData variable ... else return null and show loading bar in Fragment class
 * TODO: MAKE ASYNC DATA RETRIEVE AND FETCH DYNAMICALLY
 * TODO: convert likes -> matches on other user side
 */

class CardsViewModel(private val addToSkippedUC: AddToSkippedUseCase,
                     private val getMatchedUsersUC: GetMatchedUsersUseCase,
                     private val getPotentialUsersUC: GetPotentialUsersUseCase,
                     private val handlePossibleMatchUC: HandlePossibleMatchUseCase):
		ViewModel(){

	fun addToSkipped(skippedCardItem: CardItem) = addToSkippedUC.execute(skippedCardItem)
	fun getMatchedUserItems() = getMatchedUsersUC.execute()
	fun getPotentialUserCards() = getPotentialUsersUC.execute()
	fun handlePossibleMatch(likedCardItem: CardItem) = handlePossibleMatchUC.execute(likedCardItem)

}

