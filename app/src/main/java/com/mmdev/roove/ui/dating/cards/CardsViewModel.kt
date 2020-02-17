/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.02.20 15:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.cards

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.CheckMatchUseCase
import com.mmdev.business.cards.usecase.GetUsersByPreferencesUseCase
import com.mmdev.business.user.UserItem
import com.mmdev.roove.ui.core.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CardsViewModel @Inject constructor(private val addToSkippedUC: AddToSkippedUseCase,
                                         private val checkMatchUC: CheckMatchUseCase,
                                         private val getUsersByPreferencesUC: GetUsersByPreferencesUseCase):
		BaseViewModel(){

	private val usersCardsList: MutableLiveData<List<UserItem>> = MutableLiveData()

	val showLoading: MutableLiveData<Boolean> = MutableLiveData()
	val showMatchDialog: MutableLiveData<Boolean> = MutableLiveData()
	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()



	fun addToSkipped(skippedUserItem: UserItem) {
		addToSkippedExecution(skippedUserItem)
		Log.wtf(TAG, "skipped card: ${skippedUserItem.baseUserInfo.name}")
	}


	fun checkMatch(likedUserItem: UserItem){
		disposables.add(checkMatchExecution(likedUserItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           showMatchDialog.value = it
                           Log.wtf(TAG, "liked card: ${likedUserItem.baseUserInfo.name}")
	                       Log.wtf(TAG, "match? + ${showMatchDialog.value}")
                       },
                       {
                           Log.wtf(TAG, "error match check: $it")
                       }))
	}

	fun loadUsersByPreferences(){
		disposables.add(getUsersByPreferencesExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoading.value = true }
            .doOnSuccess {
                if(it.isNotEmpty()) showLoading.value = false
                else showTextHelper.value = true
            }
            .subscribe({
	                       if(it.isNotEmpty()) {
		                       usersCardsList.value = it
		                       showLoading.value = false
		                       showTextHelper.value = false
	                       }
	                       Log.wtf(TAG, "pagination loaded cards: ${it.size}")
                       },
                       {
	                       Log.wtf(TAG, "get potential users error: $it")
                       }))
	}


	fun getUsersCardsList() = usersCardsList


	private fun addToSkippedExecution(skippedUserItem: UserItem) = addToSkippedUC.execute(skippedUserItem)
	private fun checkMatchExecution(likedUserItem: UserItem) = checkMatchUC.execute(likedUserItem)
	private fun getUsersByPreferencesExecution() = getUsersByPreferencesUC.execute()
}

