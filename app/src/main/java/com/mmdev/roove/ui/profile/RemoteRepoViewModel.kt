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

package com.mmdev.roove.ui.profile

import androidx.lifecycle.MutableLiveData
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.user.IUserRepository
import com.mmdev.domain.user.data.BaseUserInfo
import com.mmdev.domain.user.data.ReportType
import com.mmdev.domain.user.data.UserItem
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

@HiltViewModel
class RemoteRepoViewModel @Inject constructor(
	private val repo: IUserRepository
) : BaseViewModel() {
	
	val reportSubmittingStatus: MutableLiveData<Boolean> = MutableLiveData()
	val unmatchStatus: MutableLiveData<Boolean> = MutableLiveData()
	
	val retrievedUserItem: MutableLiveData<UserItem> = MutableLiveData()
	

	fun deleteMatchedUser(matchedUser: MatchedUserItem) {
		disposables.add(repo.deleteMatchedUser(MainActivity.currentUser!!, matchedUser)
            .observeOn(mainThread())
            .subscribe(
	            { unmatchStatus.value = true },
	            { error.value = MyError(ErrorType.DELETING, it) }
            )
		)
	}

	fun getRequestedUserInfo(baseUserInfo: BaseUserInfo) {
		disposables.add(repo.getRequestedUserItem(baseUserInfo)
            .observeOn(mainThread())
            .subscribe(
	            { retrievedUserItem.value = it },
	            { error.value = MyError(ErrorType.LOADING, it) }
            )
		)
	}

	fun submitReport(reportType: ReportType, baseUserInfo: BaseUserInfo) {
		disposables.add(repo.submitReport(reportType, baseUserInfo)
            .observeOn(mainThread())
            .subscribe(
	            { reportSubmittingStatus.value = true },
	            { error.value = MyError(ErrorType.SUBMITING, it) }
            )
		)
	}
	
}