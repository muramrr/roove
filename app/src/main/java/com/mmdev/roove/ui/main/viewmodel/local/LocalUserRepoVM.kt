package com.mmdev.roove.ui.main.viewmodel.local

import androidx.lifecycle.ViewModel
import com.mmdev.business.user.model.UserItem
import com.mmdev.business.user.usecase.local.GetSavedUserUseCase
import com.mmdev.business.user.usecase.local.SaveUserInfoUseCase

class LocalUserRepoVM (private val getSavedUser: GetSavedUserUseCase,
                       private val saveUserInfo: SaveUserInfoUseCase): ViewModel() {

	fun getSavedUser() = getSavedUser.execute()
	fun saveUserInfo(currentUserItem: UserItem) = saveUserInfo.execute(currentUserItem)

}
