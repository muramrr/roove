/*
 * Created by Andrii Kovalchuk on 30.11.19 22:00
 * Copyright (c) 2019. All rights reserved.
 * Last modified 30.11.19 21:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions.pairs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.pairs.GetMatchedUsersUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


/**
 * This is the documentation block about the class
 */

class PairsViewModel @Inject constructor(private val getMatchedUsersUC: GetMatchedUsersUseCase):
		ViewModel() {

	private val matchedUsersList: MutableLiveData<List<CardItem>> = MutableLiveData()

	private val disposables = CompositeDisposable()

	companion object {
		private const val TAG = "mylogs"
	}

	fun loadMatchedUsers() {
		disposables.add(getMatchedUsersExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if (it.isNotEmpty()) matchedUsersList.value = it
	                       Log.wtf(TAG, "pairs to show: ${it.size}")
                       },
                       {
                           Log.wtf(TAG, "error + $it")
                       }))
	}

	fun getMatchedUsersList() = matchedUsersList

	private fun getMatchedUsersExecution() = getMatchedUsersUC.execute()

	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}

}