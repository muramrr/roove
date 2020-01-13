/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.01.20 18:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.business.user.usecase.remote.DeleteUserUseCase
import com.mmdev.business.user.usecase.remote.FetchUserInfoUseCase
import com.mmdev.business.user.usecase.remote.GetFullUserItemUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class RemoteUserRepoViewModel @Inject constructor(private val deleteUserUC: DeleteUserUseCase,
                                                  private val fetchUserUC: FetchUserInfoUseCase,
                                                  private val getFullUserItemUC: GetFullUserItemUseCase) :
		ViewModel() {


	private val receivedUserItem: MutableLiveData<UserItem> = MutableLiveData()

	private val disposables = CompositeDisposable()

	companion object{
		private const val TAG = "mylogs"
	}


	fun getFullUserItem(baseUserInfo: BaseUserInfo){
		disposables.add(getFullUserItemExecution(baseUserInfo)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       receivedUserItem.value = it
                       },
                       {
                           Log.wtf(TAG, "get full user info error: $it")
                       }))
	}

	fun getUser() = receivedUserItem

	fun deleteUser(userItem: UserItem) = deleteUserUC.execute(userItem)

	private fun fetchUserInfoExecution(userItem: UserItem) = fetchUserUC.execute(userItem)

	private fun getFullUserItemExecution(baseUserInfo: BaseUserInfo) = getFullUserItemUC.execute(baseUserInfo)


	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}
}