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
import com.mmdev.domain.cards.CardsRepository
import com.mmdev.domain.user.data.UserItem
import com.mmdev.roove.core.log.logDebug
import com.mmdev.roove.core.log.logInfo
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.cards.CardsViewModel.SwipeAction.*
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError

class CardsViewModel @ViewModelInject constructor(
	private val repo: CardsRepository
): BaseViewModel() {
	
	enum class SwipeAction {
		SKIP, LIKE
	}
	
	private var cardIndex = 0
	private val usersCardsList = MutableLiveData<List<UserItem>>()

	val showLoading = MutableLiveData<Boolean>()
	val showMatchDialog = MutableLiveData<UserItem>()
	val showEmptyIndicator = MutableLiveData<Boolean>()
	
	
	val topCard = MutableLiveData<UserItem?>(null)
	val bottomCard = MutableLiveData<UserItem?>(null)
	
	init {
		loadUsersByPreferences(true)
	}

	private fun addToSkipped(skippedUser: UserItem) {
		disposables.add(repo.skipUser(MainActivity.currentUser!!, skippedUser)
            .observeOn(mainThread()).subscribe(
				{ logDebug(TAG, "Skipped: $skippedUser") },
				{ error.value = MyError(ErrorType.SUBMITING, it) }
			)
		)
	}


	private fun checkMatch(likedUser: UserItem) {
		disposables.add(repo.likeUserAndCheckMatch(MainActivity.currentUser!!, likedUser)
            .observeOn(mainThread())
            .subscribe(
	            { if (it) showMatchDialog.value = likedUser },
	            { error.value = MyError(ErrorType.CHECKING, it) }
			)
		)
	}

	private fun loadUsersByPreferences(initialLoading: Boolean = false) {
		disposables.add(repo.getUsersByPreferences(MainActivity.currentUser!!, initialLoading)
            .observeOn(mainThread())
            .doOnSubscribe { showLoading.value = true }
			.doFinally { showLoading.value = false }
            .subscribe(
				{ cards ->
					showEmptyIndicator.postValue(cards.isNullOrEmpty())
					
					if (cards.isNotEmpty()) {
						cardIndex = 0
						
						usersCardsList.postValue(cards)
						
						topCard.postValue(cards.first())
						bottomCard.postValue(cards.drop(1).firstOrNull())
						
					}
					else {
						topCard.postValue(null)
						bottomCard.postValue(null)
					}
					
					logInfo(TAG, "loaded cards: ${cards.size}")
				},
				{ error.value = MyError(ErrorType.LOADING, it) }
			)
		)
	}
	
	fun swipeTop(swipeAction: SwipeAction) {
		cardIndex += 2
		when (swipeAction) {
			SKIP -> addToSkipped(topCard.value!!)
			LIKE -> checkMatch(topCard.value!!).also {
				logInfo(TAG, "Liked top: ${topCard.value?.baseUserInfo?.name}")
			}
		}
		logInfo(TAG, "index = $cardIndex")
		
		if (cardIndex >= usersCardsList.value!!.size) loadUsersByPreferences()
		else topCard.postValue(usersCardsList.value!!.getOrNull(cardIndex))
		
	}
	
	fun swipeBottom(swipeAction: SwipeAction) {
		when (swipeAction) {
			SKIP -> addToSkipped(bottomCard.value!!)
			LIKE -> checkMatch(bottomCard.value!!).also {
				logInfo(TAG, "Liked bottom: ${bottomCard.value?.baseUserInfo?.name}")
			}
		}
		logInfo(TAG, "index = $cardIndex")
		bottomCard.postValue(usersCardsList.value!!.getOrNull(cardIndex + 1))
	}
	
}

