package com.mmdev.meetapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.user.usecase.GetSavedUserUseCase
import com.mmdev.business.user.usecase.SaveUserInfoUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class MainViewModelFactory @Inject constructor(private val getSavedUser: GetSavedUserUseCase,
                                               private val saveUserInfo: SaveUserInfoUseCase) :
		ViewModelProvider.Factory {

	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
			return MainViewModel(getSavedUser, saveUserInfo) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}