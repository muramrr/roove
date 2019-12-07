/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 07.12.19 19:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.drawerflow.view

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.user.model.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.core.FlowFragment
import com.mmdev.roove.ui.drawerflow.viewmodel.local.LocalUserRepoViewModel
import com.mmdev.roove.utils.addSystemTopPadding
import kotlinx.android.synthetic.main.fragment_flow_drawer.*
import kotlinx.android.synthetic.main.nav_header.view.*


/**
 * This is the documentation block about the class
 */

class DrawerFlowFragment: FlowFragment(R.layout.fragment_flow_drawer) {

	private lateinit var userItemModel: UserItem

	private lateinit var params: AppBarLayout.LayoutParams

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var authViewModel: AuthViewModel
	private val factory = injector.factory()

	private lateinit var navController: NavController


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
		userItemModel = ViewModelProvider(this, factory)
			.get(LocalUserRepoViewModel::class.java)
			.getSavedUser()

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		sharedViewModel.setCurrentUser(userItemModel)

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		//NavigationUI.setupWithNavController(navigationView, drawerHostFragment
		// .findNavController())
		val navHost = childFragmentManager.findFragmentById(R.id.drawerHostFragment) as NavHostFragment
		navController = navHost.findNavController()
		params = toolbar.layoutParams as AppBarLayout.LayoutParams

		setNavigationView()


		toolbar.setupWithNavController(navController, drawerLayout)

		drawer_core_container.addSystemTopPadding()
		navigationView.addSystemTopPadding()

	}

	private fun setNavigationView() {
		navigationView.getChildAt(navigationView.childCount - 1).overScrollMode = View.OVER_SCROLL_NEVER
		setUpUser()
		navController.addOnDestinationChangedListener { _, destination, _ ->  //3
			if (destination.id in arrayOf(
							R.id.profileFragment
					)) {

				appBarGone()

			} else {
				appBarShow()

			}

			if (destination.id == R.id.chatFragment){

				//toolbar.inflateMenu(R.menu.chat_menu)
			}

			if (destination.id in arrayOf(
							R.id.nav_cards,
							R.id.chatFragment
					)) {
				setNonScrollableToolbar()
			} else {
				setScrollableToolbar()
			}
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

	private fun setUpUser() {
		val navHeader = navigationView.getHeaderView(0)
		navHeader.tvSignedInUserName.text = userItemModel.name
		GlideApp.with(navHeader.ivSignedInUserAvatar.context)
			.load(userItemModel.mainPhotoUrl)
			.apply(RequestOptions().circleCrop())
			.into(navHeader.ivSignedInUserAvatar)

	}

	private fun appBarGone(){
		setScrollableToolbar()
		app_bar.setExpanded(false,true)
	}

	private fun appBarShow(){
		setScrollableToolbar()
		app_bar.setExpanded(true, false)

	}

	private fun setNonScrollableToolbar(){
		params.scrollFlags = 0
		toolbar.layoutParams = params

	}

	private fun setScrollableToolbar(){
		params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
				AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
				AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
		toolbar.layoutParams = params

	}


	override fun onBackPressed() {
		when {
			drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)

			else -> navController.navigateUp()
		}
	}





}