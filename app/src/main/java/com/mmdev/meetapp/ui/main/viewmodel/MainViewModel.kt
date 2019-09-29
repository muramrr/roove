package com.mmdev.meetapp.ui.main.viewmodel

import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mmdev.domain.core.model.User
import com.mmdev.domain.user.usecase.GetSavedUser
import com.mmdev.domain.user.usecase.SaveUserInfo

class MainViewModel (private val getSavedUser: GetSavedUser,
                     private val saveUserInfo: SaveUserInfo): ViewModel() {


	fun getSavedUser() = getSavedUser.execute()
	fun saveUserInfo(currentUser: User) = saveUserInfo.execute(currentUser)

	var Name = MutableLiveData<String>()
	val busy = MutableLiveData<Int>()

	fun init() {
		busy.value = View.GONE
	}

	private fun setBusy(visibility: Int) {
		busy.value = visibility
	}

	fun onLoginClicked() {
		setBusy(View.VISIBLE)
		val signedInUser = FirebaseAuth.getInstance().currentUser
		if (signedInUser != null && !TextUtils.isEmpty(signedInUser.displayName)) {
			Handler().postDelayed({
				                      //                mProfileModel mProfileModel = new mProfileModel(signedInUser.getDisplayName(),"Kyiv", "male", signedInUser.getEmail());
				                      //                setUserModel(mProfileModel);
				                      Name.value = signedInUser.displayName
				                      setBusy(View.GONE)
			                      }, 1000)
		}
	}

	override fun onCleared() {
		super.onCleared()
		Name.value = null
	}
}
