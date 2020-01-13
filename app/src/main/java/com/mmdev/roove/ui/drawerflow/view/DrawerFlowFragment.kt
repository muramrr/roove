/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.01.20 18:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.drawerflow.view

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.core.FlowFragment
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.core.viewmodel.LocalUserRepoViewModel
import com.mmdev.roove.utils.addSystemTopPadding
import kotlinx.android.synthetic.main.fragment_drawer_flow.*
import kotlinx.android.synthetic.main.nav_header.view.*


/**
 * This is the documentation block about the class
 */

class DrawerFlowFragment: FlowFragment(R.layout.fragment_drawer_flow) {

	private var userItemModel: UserItem? = null

	private lateinit var toolbarParams: AppBarLayout.LayoutParams
	private lateinit var appBarParams: CoordinatorLayout.LayoutParams
	private var appBarParamHeight = 0

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var authViewModel: AuthViewModel


	private lateinit var navController: NavController


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		userItemModel = ViewModelProvider(this, factory)
			.get(LocalUserRepoViewModel::class.java)
			.getSavedUser()

		activity?.run {
			sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
			authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		userItemModel?.let { sharedViewModel.setCurrentUser(it) }

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		appBarParams = app_bar.layoutParams as CoordinatorLayout.LayoutParams
		//remember appbar height
		appBarParamHeight = appBarParams.height
		toolbarParams = toolbar.layoutParams as AppBarLayout.LayoutParams

		val navHost = childFragmentManager.findFragmentById(R.id.drawerHostFragment) as NavHostFragment
		navController = navHost.findNavController()

		setNavigationView()

		toolbar.setupWithNavController(navController, drawerLayout)

		drawer_core_container.addSystemTopPadding()
		navigationView.addSystemTopPadding()

	}

	private fun setNavigationView() {
		navigationView.getChildAt(navigationView.childCount - 1).overScrollMode = View.OVER_SCROLL_NEVER

		navController.addOnDestinationChangedListener { _, destination, _ ->  //3
			if (destination.id in arrayOf(
							R.id.profileFragment,
							R.id.chatFragment
					)) {

				appBarGone()

			}
			else appBarShow()

			if (destination.id in arrayOf(
							R.id.nav_cards,
							R.id.chatFragment
					)) {
				setNonScrollableToolbar()
			}
			else setScrollableToolbar()
		}

		navigationView.setNavigationItemSelectedListener { item ->
			drawerLayout.closeDrawer(GravityCompat.START)
			// Handle navigation view item clicks here.
			when (item.itemId) {
				R.id.nav_actions -> navController.navigate(R.id.action_open_inboxFragment)
				R.id.nav_places -> navController.navigate(R.id.action_open_placesFragment)
				R.id.nav_cards -> navController.navigate(R.id.action_open_cardsFragment)
//				R.id.nav_notifications -> { progressDialog.showDialog()
//					Handler().postDelayed({ progressDialog.dismissDialog() }, 5000) }
				R.id.nav_account -> { }
				R.id.nav_log_out -> showSignOutPrompt()
			}
			return@setNavigationItemSelectedListener true
		}

		val navHeader = navigationView.getHeaderView(0)
		navHeader.tvSignedInUserName.text = userItemModel?.baseUserInfo?.name
		GlideApp.with(navHeader.ivSignedInUserAvatar.context)
			.load(userItemModel?.baseUserInfo?.mainPhotoUrl)
			.apply(RequestOptions().circleCrop())
			.into(navHeader.ivSignedInUserAvatar)

	}

	/*
	* log out pop up
	*/
	private fun showSignOutPrompt() {
		MaterialAlertDialogBuilder(context)
			.setTitle("Do you wish to log out?")
			.setMessage("This will permanently log you out.")
			.setPositiveButton("Log out") { dialog, _ ->
				authViewModel.logOut()
				dialog.dismiss()
			}
			.setNegativeButton("Cancel") { dialog, _ ->
				dialog.dismiss()
			}
			.show()

	}

	private fun appBarGone(){
		appBarParams.height = 0
		app_bar.layoutParams = appBarParams
		app_bar.setExpanded(false, true)

	}

	private fun appBarShow(){
		appBarParams.height = appBarParamHeight
		app_bar.layoutParams = appBarParams
		app_bar.setExpanded(true, false)

	}

	private fun setNonScrollableToolbar(){
		toolbarParams.scrollFlags = 0
		toolbar.layoutParams = toolbarParams
	}

	private fun setScrollableToolbar(){
		toolbarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
				AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
				AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
		toolbar.layoutParams = toolbarParams

	}


	override fun onBackPressed() {
		when {
			drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)

			else -> navController.navigateUp()
		}
	}





}