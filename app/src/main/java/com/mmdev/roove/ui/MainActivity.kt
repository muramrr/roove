/*
 * Created by Andrii Kovalchuk on 02.12.19 20:57
 * Copyright (c) 2019. All rights reserved.
 * Last modified 02.12.19 20:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.events.model.EventItem
import com.mmdev.business.user.model.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModel
import com.mmdev.roove.ui.chat.view.ChatFragment
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.custom.LoadingDialog
import com.mmdev.roove.ui.main.view.DrawerFlowFragment
import com.mmdev.roove.ui.main.viewmodel.local.LocalUserRepoViewModel
import com.mmdev.roove.ui.places.view.PlacesFragment
import com.mmdev.roove.ui.places.view.detailed.PlaceDetailedFragment
import com.mmdev.roove.ui.profile.view.ProfileFragment
import com.mmdev.roove.utils.doOnApplyWindowInsets
import com.mmdev.roove.utils.replaceFragmentInDrawer
import com.mmdev.roove.utils.showToastText
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity(R.layout.activity_main) {

	companion object{
		private const val TAG = "mylogs"
	}

	lateinit var progressDialog: LoadingDialog

	lateinit var userItemModel: UserItem

	lateinit var cardItemClicked: CardItem
	var conversationItemClicked: ConversationItem? = null

	lateinit var eventItem: EventItem

	lateinit var partnerId: String
	lateinit var partnerMainPhotoUrl: String
	lateinit var partnerName: String

	private lateinit var authViewModel: AuthViewModel
	private val factory = injector.factory()

	private val currentFragment: BaseFragment?
		get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment
	private val disposables = CompositeDisposable()


	override fun onCreate(savedInstanceState: Bundle?) {

		window.apply {
			decorView.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
			         WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

		}

		super.onCreate(savedInstanceState)

		progressDialog = LoadingDialog(this@MainActivity)

		authViewModel = ViewModelProvider(this@MainActivity, factory)[AuthViewModel::class.java]

		userItemModel = ViewModelProvider(this@MainActivity, factory)
			.get(LocalUserRepoViewModel::class.java)
			.getSavedUser()


		main_activity_container.doOnApplyWindowInsets { view, insets, initialPadding ->

			view.updatePadding(left = initialPadding.left + insets.systemWindowInsetLeft,
			                   right = initialPadding.right + insets.systemWindowInsetRight)

			insets.replaceSystemWindowInsets(Rect(0,
			                                      insets.systemWindowInsetTop,
			                                      0,
			                                      insets.systemWindowInsetBottom))
		}

		showDrawerFlowFragment()


	}

	// show main feed fragment
	private fun showDrawerFlowFragment(){
		supportFragmentManager.beginTransaction().apply {
				add(R.id.main_activity_container,
				    DrawerFlowFragment(),
				    PlacesFragment::class.java.canonicalName)
				commit()
			}

	}

	/*
	 * start chat
	 */
	fun startChatFragment(conversationId: String) {
		supportFragmentManager.replaceFragmentInDrawer(ChatFragment.newInstance(conversationId))
	}

	fun startEventDetailedFragment(){
		supportFragmentManager.replaceFragmentInDrawer(PlaceDetailedFragment.newInstance())
	}

	fun startProfileFragment(userId: String, fabVisible: Boolean) {
		supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.canonicalName) ?:
		supportFragmentManager.beginTransaction().apply {
			setCustomAnimations(R.anim.enter_from_top,
			                    R.anim.exit_to_bottom,
			                    R.anim.enter_from_bottom,
			                    R.anim.exit_to_top)
			add(R.id.main_core_container,
			        ProfileFragment.newInstance(userId, fabVisible),
			        ProfileFragment::class.java.canonicalName)
			addToBackStack(ProfileFragment::class.java.canonicalName)
			commit()
		}
	}

	fun showToast(text: String) = showToastText(text)

	override fun onBackPressed() {
		currentFragment?.onBackPressed() ?: super.onBackPressed()
	}

	override fun onDestroy() {
		disposables.clear()
		super.onDestroy()
	}



}
