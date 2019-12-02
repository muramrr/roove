/*
 * Created by Andrii Kovalchuk on 02.12.19 20:57
 * Copyright (c) 2019. All rights reserved.
 * Last modified 02.12.19 20:54
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.main.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.actions.ActionsFragment
import com.mmdev.roove.ui.cards.view.CardsFragment
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.custom.CustomAlertDialog
import com.mmdev.roove.ui.places.view.PlacesFragment
import com.mmdev.roove.utils.replaceFragmentInDrawer
import kotlinx.android.synthetic.main.drawer_flow_fragment.*
import kotlinx.android.synthetic.main.nav_header.*

/**
 * This is the documentation block about the class
 */

class DrawerFlowFragment: BaseFragment(R.layout.drawer_flow_fragment) {


	private lateinit var mMainActivity: MainActivity
	private lateinit var params: AppBarLayout.LayoutParams

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		mMainActivity.setSupportActionBar(toolbar)
		params = toolbar.layoutParams as AppBarLayout.LayoutParams
		setToolbarNavigation()
		setNavigationView()

		childFragmentManager.replaceFragmentInDrawer(PlacesFragment.newInstance())
		super.onViewCreated(view, savedInstanceState)
	}

	private fun setToolbarNavigation(){
		val toggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar,
		                                   R.string.navigation_drawer_open,
		                                   R.string.navigation_drawer_close)

		drawerLayout.addDrawerListener(toggle)
		toggle.syncState()
	}

	private fun setNavigationView() {
		navigationView.getChildAt(navigationView.childCount - 1).overScrollMode = View.OVER_SCROLL_NEVER
		//setUpUser()
		navigationView.setNavigationItemSelectedListener { item ->
			drawerLayout.closeDrawer(GravityCompat.START)
			// Handle navigation view item clicks here.
			when (item.itemId) {
				R.id.nav_actions -> childFragmentManager.replaceFragmentInDrawer(ActionsFragment.newInstance())
				R.id.nav_places -> childFragmentManager.replaceFragmentInDrawer(PlacesFragment.newInstance())
				R.id.nav_cards -> {
					childFragmentManager.replaceFragmentInDrawer(CardsFragment.newInstance())

				}
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
		val builder = CustomAlertDialog.Builder(mMainActivity)
		builder.setMessage("Do you wish to sign out?")
		builder.setPositiveBtnText("Yes")
		builder.setNegativeBtnText("NO")
		builder.onPositiveClicked(View.OnClickListener {
//			authViewModel.logOut()
//			startAuthActivity()
		})

		builder.build()

	}

	private fun setUpUser() {
		tvSignedInUserName.text = mMainActivity.userItemModel.name
		GlideApp.with(this)
			.load(mMainActivity.userItemModel.mainPhotoUrl)
			.apply(RequestOptions().circleCrop())
			.into(ivSignedInUserAvatar)

	}

	fun setNonScrollableToolbar(){
		params.scrollFlags = 0
		toolbar.layoutParams = params

	}

	fun setScrollableToolbar(){
		params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
				AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
				AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
		toolbar.layoutParams = params
	}

	override fun onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
		else super.onBackPressed()
	}



}