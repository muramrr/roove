/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.03.20 19:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.pairs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.usecase.DeleteMatchUseCase
import com.mmdev.business.pairs.usecase.GetMatchedUsersUseCase
import com.mmdev.business.pairs.usecase.GetMoreMatchedUsersListUseCase
import com.mmdev.roove.ui.common.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


/**
 * This is the documentation block about the class
 */

class PairsViewModel @Inject
constructor(private val deleteMatchUC: DeleteMatchUseCase,
            private val getMatchedUsersUC: GetMatchedUsersUseCase,
            private val getMoreMatchedUsersUC: GetMoreMatchedUsersListUseCase): BaseViewModel() {

	val matchedUsersList: MutableLiveData<MutableList<MatchedUserItem>> = MutableLiveData()
	init {
		matchedUsersList.value = mutableListOf()
	}

	private val deleteMatchStatus: MutableLiveData<Boolean> = MutableLiveData()

	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()


	fun deleteMatchedUser(matchedUser: MatchedUserItem) {
		disposables.add(deleteMatchExecution(matchedUser)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       Log.wtf(TAG, "matchedUser ${matchedUser.baseUserInfo.userId} deleted")
	                       deleteMatchStatus.value = true
                       },
                       {
	                       Log.wtf(TAG, "match delete fail, error = $it")
	                       deleteMatchStatus.value = false
                       }))
	}

	fun loadMatchedUsers() {
		disposables.add(getMatchedUsersExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if (it.isNotEmpty()) {
		                       matchedUsersList.value = it.toMutableList()
		                       showTextHelper.value = false
	                       }
	                       Log.wtf(TAG, "initial loaded pairs: ${it.size}")
                       },
                       {
	                       showTextHelper.value = true
                           Log.wtf(TAG, "error + $it")
                       }))
	}


	fun loadMoreMatchedUsers() {
		disposables.add(getMoreMatchedUsersExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           if (it.isNotEmpty()) {
	                           matchedUsersList.value!!.addAll(it)
	                           matchedUsersList.value = matchedUsersList.value
                           }
                           Log.wtf(TAG, "loaded more pairs: ${it.size}")
                       },
                       {
                           Log.wtf(TAG, "error + $it")
                       }))
	}

	fun getDeleteMatchStatus() = deleteMatchStatus


	private fun deleteMatchExecution(matchedUser: MatchedUserItem) = deleteMatchUC.execute(matchedUser)
	private fun getMatchedUsersExecution() = getMatchedUsersUC.execute()
	private fun getMoreMatchedUsersExecution() = getMoreMatchedUsersUC.execute()
}