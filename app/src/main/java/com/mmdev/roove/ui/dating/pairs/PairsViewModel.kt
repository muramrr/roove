/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.03.20 17:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.pairs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.pairs.usecase.DeleteMatchUseCase
import com.mmdev.business.pairs.usecase.GetMatchedUsersUseCase
import com.mmdev.business.pairs.usecase.GetMoreMatchedUsersListUseCase
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


/**
 * This is the documentation block about the class
 */

class PairsViewModel @Inject constructor(repo: PairsRepository): BaseViewModel() {

	private val deleteMatchUC = DeleteMatchUseCase(repo)
	private val getMatchedUsersUC = GetMatchedUsersUseCase(repo)
	private val getMoreMatchedUsersUC = GetMoreMatchedUsersListUseCase(repo)

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
	                       deleteMatchStatus.value = false
	                       error.value = MyError(ErrorType.DELETING, it)
                       }
            )
		)
	}

	fun loadMatchedUsers() {
		disposables.add(getMatchedUsersExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if (it.isNotEmpty()) {
		                       matchedUsersList.value = it.toMutableList()
		                       showTextHelper.value = false
	                       }
	                       else showTextHelper.value = true
	                       Log.wtf(TAG, "initial loaded pairs: ${it.size}")
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           if (it.isNotEmpty()) {
	                           matchedUsersList.value!!.addAll(it)
	                           matchedUsersList.value = matchedUsersList.value
                           }
                           Log.wtf(TAG, "loaded more pairs: ${it.size}")
                       },
                       {
	                       error.value = MyError(ErrorType.LOADING, it)
                       }
            )
		)
	}

	fun getDeleteMatchStatus() = deleteMatchStatus


	private fun deleteMatchExecution(matchedUser: MatchedUserItem) = deleteMatchUC.execute(matchedUser)
	private fun getMatchedUsersExecution() = getMatchedUsersUC.execute()
	private fun getMoreMatchedUsersExecution() = getMoreMatchedUsersUC.execute()
}