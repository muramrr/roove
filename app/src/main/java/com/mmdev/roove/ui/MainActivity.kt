/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.03.20 17:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.databinding.ActivityMainBinding
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.auth.AuthViewModel.AuthenticationState.AUTHENTICATED
import com.mmdev.roove.ui.auth.AuthViewModel.AuthenticationState.UNAUTHENTICATED
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import com.mmdev.roove.ui.profile.RemoteRepoViewModel.DeletingStatus.*
import com.mmdev.roove.utils.observeOnce
import com.mmdev.roove.utils.showToastText


class MainActivity: AppCompatActivity() {

	private lateinit var authViewModel: AuthViewModel
	private lateinit var remoteRepoViewModel: RemoteRepoViewModel
	private lateinit var sharedViewModel: SharedViewModel

	private val factory = injector.factory()

	companion object {
		private const val TAG = "mylogs_MainActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?) {

		window.apply {
			clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
			addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			decorView.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			//status bar and navigation bar colors assigned in style file
		}
		
		super.onCreate(savedInstanceState)

		DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding

		val navController = findNavController(R.id.flowHostFragment)

		val progressDialog = setupProgressDialog(this@MainActivity)

		remoteRepoViewModel = ViewModelProvider(this, factory)[RemoteRepoViewModel::class.java]
		sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
		authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

		//init observer
		authViewModel.authenticatedState.observe(this, Observer { authState ->
			when (authState) {
				UNAUTHENTICATED -> navController.navigate(R.id.action_global_authFlowFragment)
				AUTHENTICATED -> {
					//init shared observer
					sharedViewModel.getCurrentUser().observeOnce(this, Observer {
						userInitialized ->
						if (userInitialized != null) navController.navigate(R.id.action_global_mainFlowFragment)
					})

					//init auth dialog_loading & observer
					authViewModel.fetchUserItem()
					authViewModel.actualCurrentUserItem.observeOnce(this, Observer {
						actualUserItem -> sharedViewModel.setCurrentUser(actualUserItem)
					})

					remoteRepoViewModel.updatableCurrentUserItem.observe(this, Observer {
						updatedUser -> sharedViewModel.setCurrentUser(updatedUser)
					})
				}
				else -> showToastText("Can't get AuthStatus")
			}
		})

		authViewModel.showProgress.observe(this, Observer {
			if (it == true) progressDialog.show()
			else progressDialog.dismiss()
		})

		remoteRepoViewModel.isUserUpdatedStatus.observe(this, Observer {
			if (it) { showToastText(getString(R.string.toast_text_update_success)) }
		})

		remoteRepoViewModel.selfDeletingStatus.observe(this, Observer {
			when(it) {
				IN_PROGRESS -> progressDialog.show()
				FAILURE -> progressDialog.dismiss()
				COMPLETED -> {
					progressDialog.dismiss()
					authViewModel.logOut()
				}
				else -> { progressDialog.dismiss() }
			}
		})

		//start to listens auth status
		authViewModel.checkIsAuthenticated()

		//creating fake data on remote, do not call this on UI thread
//		for (i in 0 until 50){
//			UtilityManager.generateConversationOnRemote()
//			UtilityManager.generateMatchesOnRemote()
//		}

		//note: debug
//		val dm = DisplayMetrics()
//		windowManager.defaultDisplay.getMetrics(dm)
//		val width = dm.widthPixels.toFloat()
//		val height = dm.heightPixels.toFloat()
//		Log.wtf(TAG, "${px2Dp(height)}")

	}

	private fun setupProgressDialog(context: Context): Dialog {
		val dialog = Dialog(context)
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
		dialog.setCancelable(false)
		dialog.setContentView(R.layout.dialog_loading)
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		return dialog
	}

}
