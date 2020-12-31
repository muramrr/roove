/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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

