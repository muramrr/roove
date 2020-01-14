/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.01.20 18:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.auth.view.AuthFlowFragment
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.custom.LoadingDialog
import com.mmdev.roove.ui.drawerflow.view.DrawerFlowFragment
import com.mmdev.roove.utils.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity(R.layout.activity_main) {

	private lateinit var progressDialog: LoadingDialog

	private lateinit var authViewModel: AuthViewModel

	private val factory = injector.factory()

	private val currentFragment: BaseFragment?
		get() = supportFragmentManager.findFragmentById(R.id.main_activity_container) as? BaseFragment


	companion object{
		private const val TAG_LOG = "mylogs"
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

		main_activity_container.doOnApplyWindowInsets { view, insets, initialPadding ->
			view.updatePadding(left = initialPadding.left + insets.systemWindowInsetLeft,
			                   right = initialPadding.right + insets.systemWindowInsetRight)
			insets.replaceSystemWindowInsets(Rect(0,
			                                      insets.systemWindowInsetTop,
			                                      0,
			                                      insets.systemWindowInsetBottom))
		}

		GlideApp.with(ivMainSplashLogo.context)
			.asGif()
			.load(R.drawable.logo_loading)
			.into(ivMainSplashLogo)


		progressDialog = LoadingDialog(this@MainActivity)

		authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
		authViewModel.checkIsAuthenticated()
//		authViewModel.getAuthStatus().observeOnce(this, Observer {
//			if (it == false) {
//				showAuthFlowFragment()
//				Log.wtf(TAG_LOG, "USER IS NOT LOGGED IN")
//			}
//			else {
//				showDrawerFlowFragment()
//				Log.wtf(TAG_LOG, "USER IS LOGGED IN")
//			}
//		})
		authViewModel.getAuthStatus().observe(this, Observer {
			if (it == false) {
				showAuthFlowFragment()
				Log.wtf(TAG_LOG, "USER IS NOT LOGGED IN")
			}
			else {
				showDrawerFlowFragment()
				Log.wtf(TAG_LOG, "USER IS LOGGED IN")
			}
		})
		authViewModel.showProgress.observe(this, Observer {
			if (it == true) progressDialog.showDialog()
			else progressDialog.dismissDialog()
		})



	}

	// show auth fragment
	private fun showAuthFlowFragment() {
		supportFragmentManager.beginTransaction().remove(DrawerFlowFragment()).commit()
		supportFragmentManager.beginTransaction().apply {
			add(R.id.main_activity_container,
			    AuthFlowFragment(),
			    AuthFlowFragment::class.java.canonicalName)
			commit()
		}
		ivMainSplashLogo.visibility = View.GONE
	}

	// show main feed fragment
	private fun showDrawerFlowFragment() {
		supportFragmentManager.beginTransaction().remove(AuthFlowFragment()).commit()
		supportFragmentManager.beginTransaction().apply {
				add(R.id.main_activity_container,
				    DrawerFlowFragment(),
				    DrawerFlowFragment::class.java.canonicalName)
				commit()
			}
		ivMainSplashLogo.visibility = View.GONE

	}

	override fun onBackPressed() {
		currentFragment?.onBackPressed() ?: super.onBackPressed()

	}



}
