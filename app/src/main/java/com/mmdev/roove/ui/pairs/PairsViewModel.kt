/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.pairs

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError

class PairsViewModel @ViewModelInject constructor(
	private val repo: PairsRepository
): BaseViewModel() {

	val matchedUsersList: MutableLiveData<MutableList<MatchedUserItem>> = MutableLiveData(mutableListOf())

	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()

	fun loadMatchedUsers() {
		disposables.add(repo.getMatchedUsersList()
            .observeOn(mainThread())
            .subscribe(
	            {
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
		disposables.add(repo.getMoreMatchedUsersList()
            .observeOn(mainThread())
            .subscribe(
	            {
		            if (it.isNotEmpty()) {
		            	matchedUsersList.value!!.addAll(it)
			            matchedUsersList.value = matchedUsersList.value
		            }
	            },
	            { error.value = MyError(ErrorType.LOADING, it) }
            )
		)
	}
	
}