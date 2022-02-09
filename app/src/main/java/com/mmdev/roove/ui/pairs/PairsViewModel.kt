/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

import androidx.lifecycle.MutableLiveData
import com.mmdev.domain.PaginationDirection.*
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.pairs.PairsRepository
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PairsViewModel @Inject constructor(
	private val repo: PairsRepository
): BaseViewModel() {

	val initPairs = MutableLiveData<List<MatchedUserItem>>()
	val nextPairs = MutableLiveData<List<MatchedUserItem>>()
	val prevPairs = MutableLiveData<List<MatchedUserItem>>()

	val showTextHelper = MutableLiveData<Boolean>()
	
	init {
		loadInitMatchedUsers()
	}

	private fun loadInitMatchedUsers() {
		disposables.add(repo.getPairs(MainActivity.currentUser!!, Date(), 0, INITIAL)
            .observeOn(mainThread())
            .subscribe(
	            { pairs ->
		            initPairs.postValue(pairs)
		            showTextHelper.postValue(pairs.isEmpty())
	            },
	            { error.value = MyError(ErrorType.LOADING, it) }
            )
		)
	}
	
	fun loadNextMatchedUsers(matchDate: Date, page: Int) {
		disposables.add(repo.getPairs(MainActivity.currentUser!!, matchDate, page, NEXT)
			.observeOn(mainThread())
			.subscribe(
				{ nextPairs.postValue(it) },
				{ error.value = MyError(ErrorType.LOADING, it) }
			)
		)
	}
	
	fun loadPrevMatchedUsers(page: Int) {
		disposables.add(repo.getPairs(MainActivity.currentUser!!, Date(), page, PREVIOUS)
			.observeOn(mainThread())
			.subscribe(
				{ prevPairs.postValue(it) },
				{ error.value = MyError(ErrorType.LOADING, it) }
			)
		)
	}
	
}