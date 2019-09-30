package com.mmdev.meetapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.domain.core.model.User
import com.mmdev.domain.user.usecase.GetSavedUser
import com.mmdev.domain.user.usecase.SaveUserInfo

class MainViewModel (private val getSavedUser: GetSavedUser,
                     private val saveUserInfo: SaveUserInfo): ViewModel() {


	fun getSavedUser() = getSavedUser.execute()
	fun saveUserInfo(currentUser: User) = saveUserInfo.execute(currentUser)

}
