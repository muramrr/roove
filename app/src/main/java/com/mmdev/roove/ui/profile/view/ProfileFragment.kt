/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 05.12.19 19:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.chat.view.ChatFragment
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.ImagePagerAdapter
import com.mmdev.roove.ui.main.viewmodel.remote.RemoteUserRepoViewModel
import com.mmdev.roove.utils.replaceFragmentInDrawer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * This is the documentation block about the class
 */

class ProfileFragment: BaseFragment(R.layout.fragment_profile) {


	private val userPhotosAdapter = ImagePagerAdapter(listOf())

	private var fabVisible: Boolean = false

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var remoteRepoViewModel: RemoteUserRepoViewModel
	private val factory = injector.factory()

	private val disposables = CompositeDisposable()


	companion object{

		private const val FAB_VISIBLE_KEY = "FAB_VISIBLE"

		@JvmStatic
		fun newInstance(fabVisible: Boolean) = ProfileFragment().apply {
			arguments = Bundle().apply {
				putBoolean(FAB_VISIBLE_KEY, fabVisible)
			}
		}

	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		arguments?.let {
			fabVisible = it.getBoolean(FAB_VISIBLE_KEY)
		}

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		remoteRepoViewModel = ViewModelProvider(this, factory)[RemoteUserRepoViewModel::class.java]

		sharedViewModel.cardSelected.observe(this, Observer { carditem ->

			disposables.add(remoteRepoViewModel.getUserById(carditem.userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userItem ->
	                           userPhotosAdapter.updateData(userItem.photoURLs)
	                           collapseBarProfile.title = userItem.name},
                           {
	                           throwable ->
	                           Toast.makeText(context,
		                                     "$throwable",
		                                     Toast.LENGTH_SHORT).show()
                           }))
		})
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		viewPagerProfilePhotos.apply {
			(getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
			adapter = userPhotosAdapter
		}

		TabLayoutMediator(tlDotsIndicatorProfile, viewPagerProfilePhotos){
			_: TabLayout.Tab, _: Int ->
			//do nothing
		}.attach()

		toolbarProfile.apply {
			setNavigationOnClickListener { onBackPressed() }
			inflateMenu(R.menu.profile_view_options)
			setOnMenuItemClickListener { item ->
				when (item.itemId) {
					R.id.action_report -> { Toast.makeText(context,
					                                       "action report click",
					                                       Toast.LENGTH_SHORT).show()
					}
				}
				return@setOnMenuItemClickListener true
			}
		}



		if (fabVisible)
			fabProfileSendMessage.setOnClickListener {
				childFragmentManager.popBackStack()
				// if user is listed in matched container = conversation is not created
				// so empty string given
				childFragmentManager.replaceFragmentInDrawer(ChatFragment.newInstance())
			}
		else fabProfileSendMessage.visibility = View.GONE
	}


//	override fun onStart() {
//		super.onStart()
//		mMainActivity.appbar.visibility = View.GONE
//	}
//
//	override fun onStop() {
//		super.onStop()
//		mMainActivity.appbar.visibility = View.VISIBLE
//	}


	override fun onDestroy() {
		super.onDestroy()
		disposables.clear()
	}
}