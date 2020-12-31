/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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