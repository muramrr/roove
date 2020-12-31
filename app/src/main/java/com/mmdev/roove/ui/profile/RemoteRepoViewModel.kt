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

package com.mmdev.roove.ui.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.data.PhotoItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.remote.RemoteUserRepository
import com.mmdev.business.remote.Report
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError

/**
 * This is the documentation block about the class
 */

class RemoteRepoViewModel @ViewModelInject constructor(
	private val repo: RemoteUserRepository
) : BaseViewModel() {

	val isUserUpdatedStatus: MutableLiveData<Boolean> = MutableLiveData()
	val reportSubmittingStatus: MutableLiveData<Boolean> = MutableLiveData()
	val photoDeletingStatus: MutableLiveData<Boolean> = MutableLiveData()
	val unmatchStatus: MutableLiveData<Boolean> = MutableLiveData()
	val selfDeletingStatus: MutableLiveData<DeletingStatus> = MutableLiveData()

	val updatableCurrentUserItem: MutableLiveData<UserItem> = MutableLiveData()
	val retrievedUserItem: MutableLiveData<UserItem> = MutableLiveData()

	val photoUrls: MutableLiveData<List<PhotoItem>> = MutableLiveData()

	fun deleteMatchedUser(matchedUser: MatchedUserItem) {
		disposables.add(repo.deleteMatchedUser(matchedUser)
            .observeOn(mainThread())
            .subscribe(
	            { unmatchStatus.value = true },
	            { error.value = MyError(ErrorType.DELETING, it) }
            )
		)
	}

	fun deleteMyAccount() {
		disposables.add(repo.deleteMyself()
            .observeOn(mainThread())
            .subscribe(
	            { selfDeletingStatus.value = DeletingStatus.COMPLETED },
	            {
		            selfDeletingStatus.value = DeletingStatus.FAILURE
		            error.value = MyError(ErrorType.DELETING, it)
	            }
            )
		)
	}

	fun deletePhoto(photoItem: PhotoItem, userItem: UserItem, isMainPhotoDeleting: Boolean) {
		disposables.add(repo.deletePhoto(photoItem, userItem, isMainPhotoDeleting)
            .observeOn(mainThread())
            .subscribe(
	            { photoDeletingStatus.value = true },
	            {
		            photoDeletingStatus.value = false
		            error.value = MyError(ErrorType.DELETING, it)
	            }
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

	fun submitReport(report: Report) {
		disposables.add(repo.submitReport(report)
            .observeOn(mainThread())
            .subscribe(
	            { reportSubmittingStatus.value = true },
	            { error.value = MyError(ErrorType.SUBMITING, it) }
            )
		)
	}

	fun updateUserItem(userItem: UserItem) {
		disposables.add(repo.updateUserItem(userItem)
            .observeOn(mainThread())
            .subscribe(
	            {
		            isUserUpdatedStatus.value = true
		            updatableCurrentUserItem.value = userItem
	            },
	            {
		            isUserUpdatedStatus.value = false
		            error.value = MyError(ErrorType.SAVING, it)
	            }
            )
		)
	}

	fun uploadUserProfilePhoto(photoUri: String, userItem: UserItem) {
		disposables.add(repo.uploadUserProfilePhoto(photoUri, userItem)
            .observeOn(mainThread())
            .subscribe(
	            {
		            if (it.containsKey(100.00)) photoUrls.value = it.getValue(100.00)
	            // else Log.wtf(TAG, "Upload is ${"%.2f".format(it.keys.elementAt(0))}%
	            // done")
	            },
	            { error.value = MyError(ErrorType.UPLOADING, it) }
            )
		)
	}

	enum class DeletingStatus { IN_PROGRESS, COMPLETED, FAILURE }
}