/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.04.20 17:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.pairs

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.pairs.usecase.GetMatchedUsersUseCase
import com.mmdev.business.pairs.usecase.GetMoreMatchedUsersListUseCase
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject

class PairsViewModel @Inject constructor(repo: PairsRepository): BaseViewModel() {

	private val getMatchedUsersUC = GetMatchedUsersUseCase(repo)
	private val getMoreMatchedUsersUC = GetMoreMatchedUsersListUseCase(repo)

	val matchedUsersList: MutableLiveData<MutableList<MatchedUserItem>> = MutableLiveData(mutableListOf())

	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()

	fun loadMatchedUsers() {
		disposables.add(getMatchedUsersExecution()
            .observeOn(mainThread())
            .subscribe({
	                       if (it.isNotEmpty()) {
		                       matchedUsersList.value = it.toMutableList()
		                       showTextHelper.value = false
	                       }
	                       else showTextHelper.value = true
                       },
                       {
	                       showTextHelper.value = true
	                       error.value = MyError(ErrorType.LOADING, it)
                       }
            )
		)
	}


	fun loadMoreMatchedUsers() {
		disposables.add(getMoreMatchedUsersExecution()
            .observeOn(mainThread())
            .subscribe({
                           if (it.isNotEmpty()) {
	                           matchedUsersList.value!!.addAll(it)
	                           matchedUsersList.value = matchedUsersList.value
                           }
                       },
                       {
	                       error.value = MyError(ErrorType.LOADING, it)
                       }
            )
		)
	}

	private fun getMatchedUsersExecution() = getMatchedUsersUC.execute()
	private fun getMoreMatchedUsersExecution() = getMoreMatchedUsersUC.execute()
}