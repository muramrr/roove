/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.02.20 18:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.user.UserItem
import com.mmdev.business.user.usecase.remote.DeleteUserUseCase
import com.mmdev.business.user.usecase.remote.FetchUserInfoUseCase
import com.mmdev.business.user.usecase.remote.UpdateUserItemUseCase
import com.mmdev.business.user.usecase.remote.UploadUserProfilePhotoUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class RemoteUserRepoViewModel @Inject constructor(
	private val deleteUserUC: DeleteUserUseCase,
	private val fetchUserUC: FetchUserInfoUseCase,
	private val updateUserItemUC: UpdateUserItemUseCase,
	private val uploadUserProfilePhotoUC: UploadUserProfilePhotoUseCase) : ViewModel() {


	private val fetchedUserItem: MutableLiveData<UserItem> = MutableLiveData()
	private val isUserUpdated: MutableLiveData<Boolean> = MutableLiveData()

	val photoURLs: MutableLiveData<List<String>> = MutableLiveData()

	private val disposables = CompositeDisposable()

	companion object{
		private const val TAG = "mylogs_RemoteRepoViewModel"
	}


	fun fetchUserItem(userItem: UserItem) {
		disposables.add(fetchUserInfoExecution(userItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       fetchedUserItem.value = it
	                       //Log.wtf(TAG, "fetched user: $it")
                       },
                       {
                           Log.wtf(TAG, "fetch user error: $it")
                       }))
	}

	fun updateUserItem(userItem: UserItem) {
		disposables.add(updateUserItemExecution(userItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       isUserUpdated.value = true
	                       Log.wtf(TAG, "update user successful")
                       },
                       {
	                       isUserUpdated.value = false
	                       Log.wtf(TAG, "updating user fail: $it")
                       }))
	}

	fun uploadUserProfilePhoto(photoUri: String, userItem: UserItem) {
		disposables.add(uploadUserProfilePhotoExecution(photoUri, userItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if (it.containsKey(100.00)) photoURLs.value = it.getValue(100.00)
	                       else Log.wtf(TAG, "Upload is ${"%.2f".format(it.keys.elementAt(0))}% done")
                       },
                       {
	                       Log.wtf(TAG, "uploading photo error: $it")
                       }))
	}

	fun getFetchedUserItem() = fetchedUserItem

	fun getUserUpdateStatus() = isUserUpdated



	private fun deleteUserExecution(userItem: UserItem) =
		deleteUserUC.execute(userItem)
	private fun fetchUserInfoExecution(userItem: UserItem) =
		fetchUserUC.execute(userItem)
	private fun updateUserItemExecution(userItem: UserItem) =
		updateUserItemUC.execute(userItem)
	private fun uploadUserProfilePhotoExecution(photoUri: String, userItem: UserItem) =
		uploadUserProfilePhotoUC.execute(photoUri, userItem)




	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}
}