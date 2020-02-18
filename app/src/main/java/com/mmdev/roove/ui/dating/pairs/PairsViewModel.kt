/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.02.20 18:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.pairs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.pairs.GetMatchedUsersUseCase
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.roove.ui.core.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


/**
 * This is the documentation block about the class
 */

class PairsViewModel @Inject constructor(private val getMatchedUsersUC: GetMatchedUsersUseCase):
		BaseViewModel() {

	private val matchedUsersList: MutableLiveData<MutableList<MatchedUserItem>> = MutableLiveData()
	init {
		matchedUsersList.value = mutableListOf()
	}

	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()


	fun loadMatchedUsers() {
		disposables.add(getMatchedUsersExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if (it.isNotEmpty()) {
		                       matchedUsersList.value?.addAll(it)
		                       matchedUsersList.value = matchedUsersList.value
		                       showTextHelper.value = false
	                       }
	                       else showTextHelper.value = true
	                       Log.wtf(TAG, "pairs to show: ${it.size}")
                       },
                       {
                           Log.wtf(TAG, "error + $it")
                       }))
	}

	fun getMatchedUsersList() = matchedUsersList

	private fun getMatchedUsersExecution() = getMatchedUsersUC.execute()
}