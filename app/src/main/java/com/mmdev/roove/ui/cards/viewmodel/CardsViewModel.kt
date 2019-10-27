package com.mmdev.roove.ui.cards.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.GetPotentialUserCardsUseCase
import com.mmdev.business.cards.usecase.HandlePossibleMatchUseCase
import com.mmdev.business.user.model.UserItem

/* Created by A on 20.07.2019.*/

/**
 * get users from firebase firestore
 * getAllUsers -> getSkipedUsers -> getLikedUsers -> getMatchedUsers -> mergeLikedSkipedMatched ->
 * -> create new list from getAllUsersCards list that does not contains mergedLikedSkipedMatched items
 * -> postValue into LiveData variable ... else return null and show loading bar in Fragment class
 * TODO: MAKE ASYNC DATA RETRIEVE AND FETCH DYNAMICALLY
 * TODO: convert likes -> matches on other user side
 */

class CardsViewModel(private val addToSkipped: AddToSkippedUseCase,
                     private val getPotentialUserCards: GetPotentialUserCardsUseCase,
                     private val handlePossibleMatch: HandlePossibleMatchUseCase):
		ViewModel(){

	fun addToSkipped(skippedUserItem: UserItem) = addToSkipped.execute(skippedUserItem)
	fun getPotentialUserCards() = getPotentialUserCards.execute()
	fun handlePossibleMatch(likedUserItem: UserItem) = handlePossibleMatch.execute(likedUserItem)

}

