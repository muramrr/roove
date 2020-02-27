/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.02.20 15:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.ImagePagerAdapter
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * This is the documentation block about the class
 */

class ProfileFragment: BaseFragment(R.layout.fragment_profile) {


	private val userPhotosAdapter = ImagePagerAdapter(listOf())
	private val mPlacesToGoAdapter = PlacesToGoAdapter(listOf())

	private var fabVisible: Boolean = false

	private lateinit var selectedUser: UserItem
	private lateinit var conversationId: String

	private lateinit var sharedViewModel: SharedViewModel


	companion object{
		private const val FAB_VISIBLE_KEY = "FAB_VISIBLE"
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		arguments?.let {
			fabVisible = it.getBoolean(FAB_VISIBLE_KEY)
		}

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		//if true -> seems that we navigates here from pairs fragment
		if (fabVisible) {
			sharedViewModel.matchedUserItemSelected.observeOnce(this, Observer {
				conversationId = it.conversationId
				selectedUser = it.userItem
				//ui
				collapseBarProfile.title = selectedUser.baseUserInfo.name
				userPhotosAdapter.updateData(selectedUser.photoURLs.toList())
				mPlacesToGoAdapter.updateData(selectedUser.placesToGo.toList())
			})
		}
		//else we navigates here from cards or chat fragment
		else {
			sharedViewModel.userSelected.observeOnce(this, Observer {
				selectedUser = it
				//ui
				userPhotosAdapter.updateData(selectedUser.photoURLs.toList())
				mPlacesToGoAdapter.updateData(selectedUser.placesToGo.toList())

			})
		}
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
			//menu declared directly in xml
			//no need to inflate menu manually
			//set only title and actions
			setNavigationOnClickListener { findNavController().navigateUp() }
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

		rvProfileWantToGoList.apply { adapter = mPlacesToGoAdapter }

		mPlacesToGoAdapter.setOnItemClickListener(object: PlacesToGoAdapter.OnItemClickListener {

			override fun onItemClick(view: View, position: Int) {

				val placeId = bundleOf("PLACE_ID" to
						                       mPlacesToGoAdapter.getPlaceToGoItem(position).id)

				findNavController().navigate(R.id.action_profile_to_placeDetailedFragment, placeId)

			}

		})

		if (fabVisible) {
			fabProfileSendMessage.setOnClickListener {

				findNavController().navigate(R.id.action_profile_to_chatFragment)

				sharedViewModel.setConversationSelected(ConversationItem(selectedUser,
				                                                         conversationId,
				                                                         false))
			}
		}
		else fabProfileSendMessage.visibility = View.GONE

	}

	override fun onResume() {
		super.onResume()
		if (this::selectedUser.isInitialized){
			tvProfileAboutText.text = selectedUser.aboutText
			collapseBarProfile.title = selectedUser.baseUserInfo.name
		}

	}

	override fun onBackPressed() {
		findNavController().navigateUp()
	}


}