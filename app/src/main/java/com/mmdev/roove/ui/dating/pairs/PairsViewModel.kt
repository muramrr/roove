/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.02.20 20:03
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
import com.mmdev.roove.ui.core.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


/**
 * This is the documentation block about the class
 */

class PairsViewModel @Inject constructor(private val deleteMatchUC: DeleteMatchUseCase,
                                         private val getMatchedUsersUC: GetMatchedUsersUseCase):
		BaseViewModel() {

	private val matchedUsersList: MutableLiveData<MutableList<MatchedUserItem>> = MutableLiveData()
	init {
		matchedUsersList.value = mutableListOf()
	}

	private val deleteMatchStatus: MutableLiveData<Boolean> = MutableLiveData()

	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()


	fun deleteMatchedUser(matchedUser: MatchedUserItem) {
		disposables.add(deleteMatchExecution(matchedUser)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       Log.wtf(TAG, "matchedUser ${matchedUser.userItem.baseUserInfo.userId} deleted")
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
		                       matchedUsersList.value?.addAll(it)
		                       matchedUsersList.value = matchedUsersList.value
		                       showTextHelper.value = false
	                       }
	                       else if (matchedUsersList.value?.isEmpty()!!) showTextHelper.value = true
	                       Log.wtf(TAG, "loaded pairs: ${it.size}")
                       },
                       {
                           Log.wtf(TAG, "error + $it")
                       }))
	}

	fun getDeleteMatchStatus() = deleteMatchStatus

	fun getMatchedUsersList() = matchedUsersList



	private fun deleteMatchExecution(matchedUser: MatchedUserItem) = deleteMatchUC.execute(matchedUser)
	private fun getMatchedUsersExecution() = getMatchedUsersUC.execute()
}