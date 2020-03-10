/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.03.20 20:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.UserItem
import com.mmdev.business.places.BasePlaceInfo
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentProfileBinding
import com.mmdev.roove.ui.common.ImagePagerAdapter
import com.mmdev.roove.ui.common.base.BaseAdapter
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import com.mmdev.roove.utils.observeOnce
import com.mmdev.roove.utils.showToastText
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * This is the documentation block about the class
 */

class ProfileFragment: BaseFragment<RemoteRepoViewModel>() {


	private val userPhotosAdapter = ImagePagerAdapter(listOf())

	private val mPlacesToGoAdapter = PlacesToGoAdapter(listOf(),
	                                                   R.layout.fragment_profile_places_rv_item)

	private var fabVisible: Boolean = false

	private lateinit var selectedUser: UserItem
	private lateinit var conversationId: String



	companion object{
		private const val FAB_VISIBLE_KEY = "FAB_VISIBLE"
		private const val PLACE_ID_KEY = "PLACE_ID"
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		associatedViewModel = getViewModel()
		arguments?.let {
			fabVisible = it.getBoolean(FAB_VISIBLE_KEY)
		}

		//if true -> seems that we navigates here from pairs fragment
		if (fabVisible) {
			sharedViewModel.matchedUserItemSelected.observeOnce(this, Observer {
				conversationId = it.conversationId
				associatedViewModel.getFullUserInfo(it.baseUserInfo)
			})
		}
		//else we navigates here from cards or chat fragment
		else {
			sharedViewModel.userSelected.observeOnce(this, Observer {
				associatedViewModel.getFullUserInfo(it.baseUserInfo)
			})
		}

		associatedViewModel.retrievedUserItem.observeOnce(this, Observer {
			selectedUser = it
			//ui
			userPhotosAdapter.setData(it.photoURLs.map { photoItem -> photoItem.fileUrl }.toList())

		})
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentProfileBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@ProfileFragment
				viewModel = associatedViewModel
				executePendingBindings()
			}.root

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
					R.id.action_report -> { context.showToastText("Action report clicked") }
				}
				return@setOnMenuItemClickListener true
			}
		}

		rvProfileWantToGoList.apply { adapter = mPlacesToGoAdapter }

		mPlacesToGoAdapter.setOnItemClickListener(object: BaseAdapter.OnItemClickListener<BasePlaceInfo> {

			override fun onItemClick(item: BasePlaceInfo, position: Int) {
				val placeId = bundleOf(PLACE_ID_KEY to item.id)
				findNavController().navigate(R.id.action_profile_to_placeDetailedFragment, placeId)
			}
		})

		if (fabVisible) {
			fabProfileSendMessage.setOnClickListener {

				findNavController().navigate(R.id.action_profile_to_chatFragment)
				sharedViewModel.setConversationSelected(ConversationItem(selectedUser.baseUserInfo,
				                                                         conversationId,
				                                                         false))
			}
		}
		else fabProfileSendMessage.visibility = View.GONE

	}

	override fun onBackPressed() {
		findNavController().navigateUp()
	}

}