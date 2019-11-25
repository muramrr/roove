/*
 * Created by Andrii Kovalchuk on 25.11.19 20:00
 * Copyright (c) 2019. All rights reserved.
 * Last modified 25.11.19 20:00
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
import com.mmdev.business.cards.usecase.GetMatchedUsersUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


/**
 * This is the documentation block about the class
 */

class PairsViewModel @Inject constructor(private val getMatchedUsersUC: GetMatchedUsersUseCase):
		ViewModel() {

	private val response: MutableLiveData<List<CardItem>> = MutableLiveData()

	private val disposables = CompositeDisposable()

	companion object {
		private const val TAG = "mylogs"
	}

	fun loadMatchedUsers() {
		disposables.add(load()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           Log.wtf(TAG, "pairs to show: ${it.size}")
                           response.value = it
                       },
                       {
                           Log.wtf(TAG, "error + $it")
                       }))
	}

	fun getMatchedUsers() = response

	private fun load() = getMatchedUsersUC.execute()

	override fun onCleared() { disposables.clear() }

}