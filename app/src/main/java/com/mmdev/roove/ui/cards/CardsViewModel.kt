/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.roove.ui.cards

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.cards.CardsRepository
import com.mmdev.business.user.UserItem
import com.mmdev.roove.core.log.logDebug
import com.mmdev.roove.core.log.logInfo
import com.mmdev.roove.ui.cards.CardsViewModel.SwipeAction.*
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError

class CardsViewModel @ViewModelInject constructor(
	private val repo: CardsRepository
): BaseViewModel(){
	
	enum class SwipeAction {
		SKIP, LIKE
	}

	private val usersCardsList = MutableLiveData<List<UserItem>>()

	val showLoading = MutableLiveData<Boolean>()
	val showMatchDialog = MutableLiveData<Boolean>()
	val showEmptyIndicator = MutableLiveData<Boolean>()
	
	private var cardIndex = 0
	val topCard = MutableLiveData<UserItem?>(null)
	val bottomCard = MutableLiveData<UserItem?>(null)
	
	init {
		loadUsersByPreferences(initialLoading = true)
	}

	private fun addToSkipped(skippedUserItem: UserItem) {
		disposables.add(repo.addToSkipped(skippedUserItem)
            .observeOn(mainThread()).subscribe(
				{ logDebug(TAG, "Skipped: $skippedUserItem") },
				{ error.value = MyError(ErrorType.SUBMITING, it) }
			)
		)
	}


	private fun checkMatch(likedUserItem: UserItem) {
		disposables.add(repo.checkMatch(likedUserItem)
            .observeOn(mainThread())
            .subscribe(
				{ showMatchDialog.value = it },
				{ error.value = MyError(ErrorType.CHECKING, it) }
			)
		)
	}

	private fun loadUsersByPreferences(initialLoading: Boolean = false) {
		disposables.add(repo.getUsersByPreferences(initialLoading)
            .observeOn(mainThread())
            .doOnSubscribe { showLoading.value = true }
			.doOnError { error.value = MyError(ErrorType.LOADING, it) }
            .subscribe(
				{ cards ->
					cardIndex = 0
					
					val genericList = List(20) { cards.first() }
					
					usersCardsList.postValue(genericList)
					
					topCard.postValue(genericList.firstOrNull())
					bottomCard.postValue(genericList.drop(1).firstOrNull())
					
					showLoading.postValue(genericList.isNullOrEmpty())
					showEmptyIndicator.postValue(genericList.isNullOrEmpty())
					
					logInfo(TAG, "loaded cards: ${cards.size}")
				},
				{ error.value = MyError(ErrorType.LOADING, it) }
			)
		)
	}
	
	fun swipeTop(swipeAction: SwipeAction) {
		cardIndex += 2
		if (cardIndex > usersCardsList.value!!.size) loadUsersByPreferences()
		when (swipeAction) {
			SKIP -> addToSkipped(topCard.value!!)
			LIKE -> checkMatch(topCard.value!!).also { logDebug(TAG, "Liked: ${bottomCard.value!!}") }
		}
		
		topCard.postValue(usersCardsList.value!!.getOrNull(cardIndex))
	}
	
	fun swipeBottom(swipeAction: SwipeAction) {
		when (swipeAction) {
			SKIP -> addToSkipped(bottomCard.value!!)
			LIKE -> checkMatch(bottomCard.value!!).also { logDebug(TAG, "Liked: ${bottomCard.value!!}") }
		}
		bottomCard.postValue(usersCardsList.value!!.getOrNull(cardIndex + 1))
	}
	
}

