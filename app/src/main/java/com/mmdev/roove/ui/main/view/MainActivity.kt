/*
 * Created by Andrii Kovalchuk on 20.08.19 13:37
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.11.19 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.user.model.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.actions.ActionsFragment
import com.mmdev.roove.ui.auth.view.AuthActivity
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModel
import com.mmdev.roove.ui.cards.view.CardsFragment
import com.mmdev.roove.ui.chat.view.ChatFragment
import com.mmdev.roove.ui.custom.CustomAlertDialog
import com.mmdev.roove.ui.custom.LoadingDialog
import com.mmdev.roove.ui.feed.FeedFragment
import com.mmdev.roove.ui.main.viewmodel.local.LocalUserRepoVM
import com.mmdev.roove.ui.profile.view.ProfileFragment
import com.mmdev.roove.utils.showToastText
import io.reactivex.disposables.CompositeDisposable

class MainActivity: AppCompatActivity(R.layout.activity_main),
                    MainActivityListeners {

	companion object{
		private const val TAG = "mylogs"
	}

	lateinit var progressDialog: LoadingDialog

	private lateinit var drawerLayout: DrawerLayout
	lateinit var toolbar: Toolbar
	lateinit var appbar: AppBarLayout
	private lateinit var params: AppBarLayout.LayoutParams

	private lateinit var ivSignedInUserAvatar: ImageView
	private lateinit var tvSignedInUserName: TextView

	lateinit var userItemModel: UserItem

	lateinit var cardItemClicked: CardItem
	lateinit var conversationItemClicked: ConversationItem

	lateinit var partnerId: String
	lateinit var partnerMainPhotoUrl: String
	lateinit var partnerName: String

	private lateinit var mFragmentManager: FragmentManager

	private lateinit var authViewModel: AuthViewModel
	private val authViewModelFactory = injector.authViewModelFactory()
	private val mainViewModelFactory = injector.localUserRepoVMFactory()
	private val disposables = CompositeDisposable()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		FirebaseAnalytics.getInstance(this@MainActivity)
		authViewModel = ViewModelProvider(this@MainActivity, authViewModelFactory)
			.get(AuthViewModel::class.java)

		userItemModel = ViewModelProvider(this@MainActivity, mainViewModelFactory)
			.get(LocalUserRepoVM::class.java)
			.getSavedUser()

		drawerLayout = findViewById(R.id.drawer_layout)
		appbar = findViewById(R.id.app_bar)
		toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)
		params = toolbar.layoutParams as AppBarLayout.LayoutParams

		setToolbarNavigation()
		setNavigationView()

		mFragmentManager = supportFragmentManager
		showFeedFragment()

		progressDialog = LoadingDialog(this@MainActivity)


	}

	// show main feed fragment
	private fun showFeedFragment(){
		if (mFragmentManager.findFragmentByTag(FeedFragment::class.java.canonicalName) == null)
			mFragmentManager.beginTransaction().apply {
				add(R.id.main_container,
				    FeedFragment.newInstance(),
				    FeedFragment::class.java.canonicalName)
				commit()
			}
		else mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

	}

	override fun onCardsClick() = startCardFragment()

	override fun onLogOutClick() = showSignOutPrompt()

	private fun startActionsFragment(){ replaceFragment(ActionsFragment.newInstance()) }

	/*
	 * start card swipe
	 */
	private fun startCardFragment() { replaceFragment(CardsFragment()) }

	/*
	 * start chat
	 */
	fun startChatFragment(conversationId: String) {
		replaceFragment(ChatFragment.newInstance(conversationId))
	}

	fun startProfileFragment(userId: String, fabVisible: Boolean) {
		mFragmentManager.findFragmentByTag(ProfileFragment::class.java.canonicalName) ?:
		mFragmentManager.beginTransaction().apply {
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

	private fun replaceFragment (fragment: Fragment) {
		val fragmentName = fragment.javaClass.name
		val fragmentPopped = mFragmentManager.popBackStackImmediate(fragmentName, 0)

		if (!fragmentPopped){ //fragment not in back stack, create it.
			mFragmentManager.beginTransaction().apply {
				setCustomAnimations(R.anim.enter_from_right,
				                    R.anim.exit_to_left,
				                    R.anim.enter_from_left,
				                    R.anim.exit_to_right)
				replace(R.id.main_container, fragment, fragmentName)
				addToBackStack(fragmentName)
				commit()
			}
		}
	}

	override fun startAuthActivity(){
		val authIntent = Intent(this@MainActivity, AuthActivity::class.java)
		startActivity(authIntent)
		finish()
	}

	/*
	* log out pop up
	*/
	private fun showSignOutPrompt() {
		val builder = CustomAlertDialog.Builder(this@MainActivity)
		builder.setMessage("Do you wish to sign out?")
		builder.setPositiveBtnText("Yes")
		builder.setNegativeBtnText("NO")
		builder.OnPositiveClicked(View.OnClickListener {
			Log.wtf(TAG, "USER PROMT TO LOG OUT")
			authViewModel.logOut()
			startAuthActivity()
		})

		builder.build()

	}

	private fun setUpUser() {
		tvSignedInUserName.text = userItemModel.name
		GlideApp.with(this@MainActivity)
			.load(userItemModel.mainPhotoUrl)
			.apply(RequestOptions().circleCrop())
			.into(ivSignedInUserAvatar)

	}

	private fun setToolbarNavigation(){
		val toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, toolbar,
		                                   R.string.navigation_drawer_open,
		                                   R.string.navigation_drawer_close)

		drawerLayout.addDrawerListener(toggle)
		toggle.syncState()
	}

	private fun setNavigationView() {
		val navView: NavigationView = findViewById(R.id.nav_view)
		navView.getChildAt(navView.childCount - 1).overScrollMode = View.OVER_SCROLL_NEVER
		val headerView = navView.getHeaderView(0)
		tvSignedInUserName = headerView.findViewById(R.id.signed_in_username_tv)
		ivSignedInUserAvatar = headerView.findViewById(R.id.signed_in_user_image_view)
		setUpUser()
		navView.setNavigationItemSelectedListener { item ->
			drawerLayout.closeDrawer(GravityCompat.START)
			// Handle navigation view item clicks here.
			when (item.itemId) {
				R.id.nav_actions -> startActionsFragment()
				R.id.nav_feed -> { showFeedFragment() }
				R.id.nav_cards -> onCardsClick()
				R.id.nav_notifications -> { progressDialog.showDialog()
					Handler().postDelayed({ progressDialog.dismissDialog() }, 5000) }
				R.id.nav_account -> { }
				R.id.nav_log_out -> onLogOutClick()
			}
			return@setNavigationItemSelectedListener true
		}

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

	fun showToast(text: String) = showToastText(text)

	override fun onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
		else super.onBackPressed()
	}

	override fun onDestroy() {
		super.onDestroy()
		disposables.clear()
	}



}
