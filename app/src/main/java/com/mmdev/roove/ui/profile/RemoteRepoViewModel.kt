/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 17:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.PhotoItem
import com.mmdev.business.core.UserItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.remote.RemoteUserRepository
import com.mmdev.business.remote.usecase.*
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class RemoteRepoViewModel @Inject constructor(repo: RemoteUserRepository) : BaseViewModel() {

	private val deleteMatchUC = DeleteMatchedUserUseCase(repo)
	private val deletePhotoUC = DeletePhotoUseCase(repo)
	private val deleteMyselfUC = DeleteMyselfUseCase(repo)
	private val getRequestedUserInfoUC = GetRequestedUserInfoUseCase(repo)
	private val updateUserItemUC = UpdateUserItemUseCase(repo)
	private val uploadUserProfilePhotoUC = UploadUserProfilePhotoUseCase(repo)


	val updatableCurrentUserItem: MutableLiveData<UserItem> = MutableLiveData()
	val retrievedUserItem: MutableLiveData<UserItem> = MutableLiveData()
	val isUserUpdatedStatus: MutableLiveData<Boolean> = MutableLiveData()

	val photoDeletionStatus: MutableLiveData<Boolean> = MutableLiveData()
	val photoUrls: MutableLiveData<List<PhotoItem>> = MutableLiveData()

	fun deleteMatchedUser(matchedUser: MatchedUserItem) {
		disposables.add(deleteMatchExecution(matchedUser)
            .observeOn(mainThread())
            .subscribe({
                           Log.wtf(TAG, "matchedUser ${matchedUser.baseUserInfo.userId} deleted")
                       },
                       {
                           error.value = MyError(ErrorType.DELETING, it)
                       }
            )
		)
	}

	fun deletePhoto(photoItem: PhotoItem, userItem: UserItem, isMainPhotoDeleting: Boolean) {
		disposables.add(deletePhotoExecution(photoItem, userItem, isMainPhotoDeleting)
            .observeOn(mainThread())
            .subscribe({
	                       photoDeletionStatus.value = true
	                       Log.wtf(TAG, "photo deleted")
                       },
                       {
	                       photoDeletionStatus.value = false
	                       error.value = MyError(ErrorType.DELETING, it)
                       }))
	}

	fun getRequestedUserInfo(baseUserInfo: BaseUserInfo) {
		disposables.add(getRequestedUserInfoExecution(baseUserInfo)
            .observeOn(mainThread())
            .subscribe({
                            retrievedUserItem.value = it
                       },
                       {
	                       error.value = MyError(ErrorType.LOADING, it)
                       }))

	}

	fun updateUserItem(userItem: UserItem) {
		disposables.add(updateUserItemExecution(userItem)
            .observeOn(mainThread())
            .subscribe({
	                       isUserUpdatedStatus.value = true
	                       updatableCurrentUserItem.value = userItem
	                       Log.wtf(TAG, "update user successful")
                       },
                       {
	                       isUserUpdatedStatus.value = false
	                       error.value = MyError(ErrorType.SAVING, it)
                       }))
	}

	fun uploadUserProfilePhoto(photoUri: String, userItem: UserItem) {
		disposables.add(uploadUserProfilePhotoExecution(photoUri, userItem)
            .observeOn(mainThread())
            .subscribe({
	                       if (it.containsKey(100.00)) photoUrls.value = it.getValue(100.00)
	                       //else Log.wtf(TAG, "Upload is ${"%.2f".format(it.keys.elementAt(0))}%
	                       // done")
                       },
                       {
	                       error.value = MyError(ErrorType.UPLOADING, it)
                       }))
	}


	private fun deleteMatchExecution(matchedUser: MatchedUserItem) = deleteMatchUC.execute(matchedUser)
	private fun deletePhotoExecution(photoItem: PhotoItem, userItem: UserItem, isMainPhotoDeleting: Boolean) =
		deletePhotoUC.execute(photoItem, userItem, isMainPhotoDeleting)
	private fun deleteMyselfExecution() = deleteMyselfUC.execute()
	private fun getRequestedUserInfoExecution(baseUserInfo: BaseUserInfo) =
		getRequestedUserInfoUC.execute(baseUserInfo)
	private fun updateUserItemExecution(userItem: UserItem) =
		updateUserItemUC.execute(userItem)
	private fun uploadUserProfilePhotoExecution(photoUri: String, userItem: UserItem) =
		uploadUserProfilePhotoUC.execute(photoUri, userItem)


}