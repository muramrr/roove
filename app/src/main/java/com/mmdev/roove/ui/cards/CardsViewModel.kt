/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
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
import com.mmdev.roove.core.log.logInfo
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError

class CardsViewModel @ViewModelInject constructor(
	private val repo: CardsRepository
): BaseViewModel(){

	val usersCardsList = MutableLiveData<List<UserItem>>()

	val showLoading = MutableLiveData<Boolean>()
	val showMatchDialog = MutableLiveData<Boolean>()
	val showTextHelper = MutableLiveData<Boolean>()



	fun addToSkipped(skippedUserItem: UserItem) {
		disposables.add(repo.addToSkipped(skippedUserItem)
            .observeOn(mainThread()).subscribe(
				{},
				{ error.value = MyError(ErrorType.SUBMITING, it) }
			)
		)
	}


	fun checkMatch(likedUserItem: UserItem) {
		disposables.add(repo.checkMatch(likedUserItem)
            .observeOn(mainThread())
            .subscribe(
				{ showMatchDialog.value = it },
				{ error.value = MyError(ErrorType.CHECKING, it) }
			)
		)
	}

	fun loadUsersByPreferences(initialLoading: Boolean = false) {
		disposables.add(repo.getUsersByPreferences(initialLoading)
            .observeOn(mainThread())
            .doOnSubscribe { showLoading.value = true }
			.doOnError { error.value = MyError(ErrorType.LOADING, it) }
            .subscribe(
				{
					if(it.isNotEmpty()) {
						usersCardsList.value = it
						showLoading.value = false
						showTextHelper.value = false
					}
					else showTextHelper.value = true
					logInfo(TAG, "loaded cards: ${it.size}")
				},
				{ error.value = MyError(ErrorType.LOADING, it) }
			)
		)
	}
}

