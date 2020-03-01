/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.03.20 16:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp
import com.mmdev.roove.core.injector
import com.mmdev.roove.databinding.ActivityMainBinding
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.auth.view.AuthFlowFragment
import com.mmdev.roove.ui.core.viewmodel.RemoteUserRepoViewModel
import com.mmdev.roove.ui.core.viewmodel.SharedViewModel
import com.mmdev.roove.ui.custom.LoadingDialog
import com.mmdev.roove.ui.main.MainFlowFragment
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

	private lateinit var progressDialog: LoadingDialog

	private lateinit var authViewModel: AuthViewModel
	private lateinit var remoteRepoViewModel: RemoteUserRepoViewModel
	private lateinit var sharedViewModel: SharedViewModel

	private val factory = injector.factory()

	companion object{
		private const val TAG = "mylogs_MainActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?) {

		window.apply {
			clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
			addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			decorView.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			//status bar and navigation bar colors assigned in theme
		}
		
		super.onCreate(savedInstanceState)

		val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

		GlideApp.with(ivMainSplashLogo.context)
			.asGif()
			.load(R.drawable.logo_loading)
			.into(ivMainSplashLogo)


		progressDialog = LoadingDialog(this@MainActivity)

		remoteRepoViewModel = ViewModelProvider(this, factory)[RemoteUserRepoViewModel::class.java]
		sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
		authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

		authViewModel.checkIsAuthenticated()
		authViewModel.getAuthStatus().observe(this, Observer {
			if (it == false) {
				showAuthFlowFragment()
				Log.wtf(TAG, "USER IS NOT LOGGED IN")
			}
			else {
				showMainFlowFragment()
				remoteRepoViewModel.fetchUserItem()
				remoteRepoViewModel.getFetchedUserItem().observeOnce(this, Observer {
					fetchedUser -> sharedViewModel.setCurrentUser(fetchedUser)
				})

				Log.wtf(TAG, "USER IS LOGGED IN")
			}
		})
		authViewModel.showProgress.observe(this, Observer {
			if (it == true) progressDialog.showDialog()
			else progressDialog.dismissDialog()
		})

		//creating fake users, do not call this on UI thread
//		for (i in 0 until 200){
//			UtilityManager.generateMatchesOnRemote()
//		}


	}

	// show auth fragment
	private fun showAuthFlowFragment() {
		supportFragmentManager.beginTransaction().remove(MainFlowFragment()).commitNow()
		supportFragmentManager.beginTransaction().apply {
			replace(R.id.main_activity_container,
			    AuthFlowFragment(),
			    AuthFlowFragment::class.java.canonicalName)
			commit()
		}
		ivMainSplashLogo.visibility = View.GONE
	}

	// show main fragment
	private fun showMainFlowFragment() {
		supportFragmentManager.beginTransaction().remove(AuthFlowFragment()).commitNow()
		supportFragmentManager.beginTransaction().apply {
				replace(R.id.main_activity_container,
				    MainFlowFragment(),
				    MainFlowFragment::class.java.canonicalName)
				commit()
			}
		ivMainSplashLogo.visibility = View.GONE

	}

}
