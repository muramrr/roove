package com.mmdev.roove.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.user.model.User
import com.mmdev.business.user.usecase.GetSavedUserUseCase
import com.mmdev.business.user.usecase.SaveUserInfoUseCase

class MainViewModel (private val getSavedUser: GetSavedUserUseCase,
                     private val saveUserInfo: SaveUserInfoUseCase): ViewModel() {

	fun getSavedUser() = getSavedUser.execute()
	fun saveUserInfo(currentUser: User) = saveUserInfo.execute(currentUser)

}
