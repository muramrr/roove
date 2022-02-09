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

package com.mmdev.roove.ui.settings

import androidx.lifecycle.MutableLiveData
import com.mmdev.domain.photo.PhotoItem
import com.mmdev.domain.user.ISettingsRepository
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.ErrorType.DELETING
import com.mmdev.roove.ui.common.errors.MyError
import com.mmdev.roove.ui.settings.SettingsViewModel.DeletingStatus.IN_PROGRESS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel to edit user info, basically allows to start add/delete photo or delete account
 */

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val repo: ISettingsRepository
): BaseViewModel() {
	
	
	val selfDeletingStatus: MutableLiveData<DeletingStatus> = MutableLiveData()
	
	val newPhoto = MutableLiveData<PhotoItem>()
	
	fun deleteMyAccount() {
		disposables.add(repo.deleteMyself(MainActivity.currentUser!!)
			.doOnSubscribe { selfDeletingStatus.postValue(IN_PROGRESS) }
            .observeOn(mainThread())
            .subscribe(
                { selfDeletingStatus.value = DeletingStatus.COMPLETED },
                {
	                selfDeletingStatus.value = DeletingStatus.FAILURE
	                error.value = MyError(DELETING, it)
                }
            )
		)
	}
	
	fun deletePhoto(photoItem: PhotoItem, isMainPhotoDeleting: Boolean) {
		disposables.add(repo.deletePhoto(MainActivity.currentUser!!, photoItem, isMainPhotoDeleting)
            .subscribe(
                {
	                //make new list by deleting requested photo
	                val newUrls = MainActivity.currentUser!!.photoURLs.minus(photoItem)
	
	                //update current user with new photo list
	                MainActivity.currentUser = MainActivity.currentUser!!.copy(
		                photoURLs = newUrls
	                )
	
	                //also update mainPhotoUrl if such delete operation was occured
	                if (isMainPhotoDeleting) {
		                val newBaseUserInfo = MainActivity.currentUser!!.baseUserInfo.copy(
			                mainPhotoUrl = newUrls[0].fileUrl
		                )
		                MainActivity.currentUser = MainActivity.currentUser!!.copy(
			                baseUserInfo = newBaseUserInfo
		                )
	                }
                },
                { error.value = MyError(DELETING, it) }
            )
		)
	}
	
	fun uploadUserProfilePhoto(photoUri: String) {
		disposables.add(repo.uploadUserProfilePhoto(MainActivity.currentUser!!, photoUri)
            .observeOn(mainThread())
            .subscribe(
                {
	                if (it.isNotEmpty()) {
	                	newPhoto.postValue(it.first())
		                
		                val newPhotos = MainActivity.currentUser!!.photoURLs.plus(it.first())
		                
		                //update current user with new photo list
		                MainActivity.currentUser = MainActivity.currentUser!!.copy(photoURLs = newPhotos)
	                }
                }
                ,
                { error.value = MyError(ErrorType.UPLOADING, it) }
            )
		)
	}
	
	
	enum class DeletingStatus { IN_PROGRESS, COMPLETED, FAILURE }
	
}