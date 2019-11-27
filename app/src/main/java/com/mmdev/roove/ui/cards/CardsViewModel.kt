/*
 * Created by Andrii Kovalchuk on 27.11.19 19:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 27.11.19 18:43
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.cards

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.GetUsersByPreferencesUseCase
import com.mmdev.business.cards.usecase.HandlePossibleMatchUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CardsViewModel @Inject constructor(private val addToSkippedUC: AddToSkippedUseCase,
                                         private val getUsersByPreferencesUC: GetUsersByPreferencesUseCase,
                                         private val handlePossibleMatchUC: HandlePossibleMatchUseCase):
		ViewModel(){

	private val usersCardsList: MutableLiveData<List<CardItem>> = MutableLiveData()

	val showLoading: MutableLiveData<Boolean> = MutableLiveData()
	val showMatchDialog: MutableLiveData<Boolean> = MutableLiveData()
	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()


	private val disposables = CompositeDisposable()

	companion object {
		private const val TAG = "mylogs"
	}

	fun addToSkipped(skippedCardItem: CardItem) {
		addToSkippedExecution(skippedCardItem)
		Log.wtf(TAG, "skipped + ${skippedCardItem.name}")
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
	                       Log.wtf(TAG, "cards to show: ${it.size}")
	                       usersCardsList.value = it
                       },
                       {
	                       Log.wtf(TAG, "get potential users error + $it")
                       }))
	}

	fun handlePossibleMatch(likedCardItem: CardItem){
		disposables.add(handlePossibleMatchExecution(likedCardItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           showMatchDialog.value = it
                           Log.wtf(TAG, "its a match! + ${likedCardItem.name}")
                       },
                       {
                           Log.wtf(TAG, "error swiped + $it")
                       }))
	}

	fun getUsersCardsList() = usersCardsList

	private fun addToSkippedExecution(skippedCardItem: CardItem) = addToSkippedUC.execute(skippedCardItem)

	private fun getUsersByPreferencesExecution() = getUsersByPreferencesUC.execute()

	private fun handlePossibleMatchExecution(likedCardItem: CardItem) =
		handlePossibleMatchUC.execute(likedCardItem)



	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}
}

