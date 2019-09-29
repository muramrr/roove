package com.mmdev.meetapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.domain.user.usecase.GetSavedUser
import com.mmdev.domain.user.usecase.SaveUserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class MainViewModelFactory @Inject constructor(private val getSavedUser: GetSavedUser,
                                               private val saveUserInfo: SaveUserInfo) :
		ViewModelProvider.Factory {

	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
			return MainViewModel(getSavedUser, saveUserInfo) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}