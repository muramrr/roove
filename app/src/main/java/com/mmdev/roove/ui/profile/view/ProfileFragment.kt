/*
 * Created by Andrii Kovalchuk on 22.11.19 19:36
 * Copyright (c) 2019. All rights reserved.
 * Last modified 22.11.19 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.ImagePagerAdapter
import com.mmdev.roove.ui.main.view.MainActivity
import com.mmdev.roove.ui.main.viewmodel.remote.RemoteUserRepoVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * This is the documentation block about the class
 */

class ProfileFragment: Fragment(R.layout.fragment_profile) {

	private lateinit var mMainActivity: MainActivity

	private val userPhotosAdapter = ImagePagerAdapter(listOf())

	private lateinit var remoteRepoViewModel: RemoteUserRepoVM
	private val remoteUserRepoFactory = injector.remoteUserRepoVMFactory()

	private lateinit var userId: String
	private var fabVisible: Boolean = false

	private val disposables = CompositeDisposable()


	companion object{

		private const val USER_ID_KEY = "USER_ID"
		private const val FAB_VISIBLE_KEY = "FAB_VISIBLE"

		//todo: remove data transfer between fragments, need to make it more abstract
		@JvmStatic
		fun newInstance(userId: String, fabVisible: Boolean) = ProfileFragment().apply {
			arguments = Bundle().apply {
				putBoolean(FAB_VISIBLE_KEY, fabVisible)
				putString(USER_ID_KEY, userId)
			}
		}


	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }
		arguments?.let {
			userId = it.getString(USER_ID_KEY, "")
			fabVisible = it.getBoolean(FAB_VISIBLE_KEY)
		}

		remoteRepoViewModel = ViewModelProvider(mMainActivity, remoteUserRepoFactory).get(RemoteUserRepoVM::class.java)

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val viewPager= view.findViewById<ViewPager2>(R.id.profile_photos_vp)
		val tbProfile = view.findViewById<Toolbar>(R.id.profile_toolbar)
		val tbLayout = view.findViewById<CollapsingToolbarLayout>(R.id.profile_collapsing_toolbar)
		val dots = view.findViewById<TabLayout>(R.id.dots_indicator)
		val fab = view.findViewById<FloatingActionButton>(R.id.fab_send_message)


		disposables.add(remoteRepoViewModel.getUserById(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ userPhotosAdapter.updateData(it.photoURLs)
	                    tbLayout.title = it.name},
                       { mMainActivity.showToast("$it") }))

		viewPager.adapter = userPhotosAdapter

		TabLayoutMediator(dots, viewPager){ tab: TabLayout.Tab, position: Int -> }.attach()

		tbProfile.setNavigationOnClickListener { mMainActivity.onBackPressed() }
		tbProfile.inflateMenu(R.menu.profile_view_options)
		tbProfile.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.action_report -> { Toast.makeText(mMainActivity,
				                                       "action report click",
				                                       Toast.LENGTH_SHORT).show()
				}
			}
			return@setOnMenuItemClickListener true
		}

		if (fabVisible)
			fab.setOnClickListener {
				mMainActivity.supportFragmentManager.popBackStack()
				// if user is listed in matched container = conversation is not created
				// so empty string given
				mMainActivity.startChatFragment("")

			}
		else fab.visibility = View.GONE
	}


	override fun onStart() {
		super.onStart()
		mMainActivity.appbar.visibility = View.GONE
	}

	override fun onStop() {
		super.onStop()
		mMainActivity.appbar.visibility = View.VISIBLE
	}

	override fun onDestroy() {
		super.onDestroy()
		disposables.clear()
	}
}