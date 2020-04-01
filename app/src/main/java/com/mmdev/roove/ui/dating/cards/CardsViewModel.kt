/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.04.20 17:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.cards

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.CheckMatchUseCase
import com.mmdev.business.cards.usecase.GetUsersByPreferencesUseCase
import com.mmdev.business.core.UserItem
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject

class CardsViewModel @Inject constructor(repo: CardsRepository): BaseViewModel(){


	private val addToSkippedUC = AddToSkippedUseCase(repo)
	private val checkMatchUC = CheckMatchUseCase(repo)
	private val getUsersByPreferencesUC = GetUsersByPreferencesUseCase(repo)

	val usersCardsList: MutableLiveData<List<UserItem>> = MutableLiveData()

	val showLoading: MutableLiveData<Boolean> = MutableLiveData()
	val showMatchDialog: MutableLiveData<Boolean> = MutableLiveData()
	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()



	fun addToSkipped(skippedUserItem: UserItem) {
		disposables.add(addToSkippedExecution(skippedUserItem)
            .observeOn(mainThread())
            .subscribe({},
                       {
	                       error.value = MyError(ErrorType.SUBMITING, it)
                       }))
	}


	fun checkMatch(likedUserItem: UserItem) {
		disposables.add(checkMatchExecution(likedUserItem)
            .observeOn(mainThread())
            .subscribe({
                           showMatchDialog.value = it
                       },
                       {
	                       error.value = MyError(ErrorType.CHECKING, it)
                       }))
	}

	fun loadUsersByPreferences(initialLoading: Boolean = false) {
		disposables.add(getUsersByPreferencesExecution(initialLoading)
            .observeOn(mainThread())
            .doOnSubscribe { showLoading.value = true }
            .subscribe({
	                       if(it.isNotEmpty()) {
		                       usersCardsList.value = it
		                       showLoading.value = false
		                       showTextHelper.value = false
	                       }
	                       else showTextHelper.value = true
	                       Log.wtf(TAG, "loaded cards: ${it.size}")
                       },
                       {
	                       error.value = MyError(ErrorType.LOADING, it)
                       }))
	}


	private fun addToSkippedExecution(skippedUserItem: UserItem) = addToSkippedUC.execute(skippedUserItem)
	private fun checkMatchExecution(likedUserItem: UserItem) = checkMatchUC.execute(likedUserItem)
	private fun getUsersByPreferencesExecution(initialLoading: Boolean) = getUsersByPreferencesUC.execute(initialLoading)
}

