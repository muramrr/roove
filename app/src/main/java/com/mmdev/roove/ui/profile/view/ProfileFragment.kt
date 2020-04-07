/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.04.20 13:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.business.core.UserItem
import com.mmdev.business.places.BasePlaceInfo
import com.mmdev.business.remote.entity.Report
import com.mmdev.business.remote.entity.Report.ReportType.*
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
	private val mPlacesToGoAdapter = PlacesToGoAdapter(listOf())

	private var isReported: Boolean = false
	private var fabVisible: Boolean = false
	private var isMatched: Boolean = false

	private var selectedUser: UserItem = UserItem()
	private var conversationId: String = ""


	companion object {
		private const val FAB_VISIBLE_KEY = "FAB_VISIBLE"
		private const val MATCHED_KEY = "IS_MATCHED"
		private const val PLACE_ID_KEY = "PLACE_ID"
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		associatedViewModel = getViewModel()

		arguments?.let {
			fabVisible = it.getBoolean(FAB_VISIBLE_KEY)
			isMatched = it.getBoolean(MATCHED_KEY)
		}

		associatedViewModel.retrievedUserItem.observeOnce(this, Observer {
			selectedUser = it
			//ui
			userPhotosAdapter.setData(it.photoURLs.map { photoItem -> photoItem.fileUrl })
		})


		//if true -> seems that we navigates here from pairs or chat fragment
		if (isMatched) {
			sharedViewModel.matchedUserItemSelected.value?.let {
				conversationId = it.conversationId
				associatedViewModel.getRequestedUserInfo(it.baseUserInfo)
			}
			associatedViewModel.unmatchStatus.observeOnce(this, Observer {
				if (it) navController.navigateUp()
			})
		}
		//else we navigates here from cards and already have userItem in sharedViewModel
		else {
			sharedViewModel.userNavigateTo.value?.let {
				associatedViewModel.retrievedUserItem.value = it
			}
		}

		associatedViewModel.reportSubmittingStatus.observeOnce(this, Observer {
			isReported = it
			context?.showToastText(getString(R.string.toast_text_report_success))
			toolbarProfile.apply {
				val reportItem = menu.findItem(R.id.profile_action_report)
				reportItem.isVisible = !isReported
			}
		})

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentProfileBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@ProfileFragment
				isFabVisible = fabVisible
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
			val deleteItem = menu.findItem(R.id.profile_action_unmatch)
			deleteItem.isVisible = isMatched
			setNavigationOnClickListener { navController.navigateUp() }
			setOnMenuItemClickListener { item ->
				when (item.itemId) {
					R.id.profile_action_report -> { showReportDialog() }
					R.id.profile_action_unmatch -> {
						sharedViewModel.matchedUserItemSelected.value?.let {
							associatedViewModel.deleteMatchedUser(it)
						}
					}
				}
				return@setOnMenuItemClickListener true
			}
		}

		rvProfileWantToGoList.apply { adapter = mPlacesToGoAdapter }

		mPlacesToGoAdapter.setOnItemClickListener(object: BaseAdapter.OnItemClickListener<BasePlaceInfo> {

			override fun onItemClick(item: BasePlaceInfo, position: Int) {
				val placeId = bundleOf(PLACE_ID_KEY to item.id)
				navController.navigate(R.id.action_profile_to_placeDetailedFragment, placeId)
			}
		})

		if (fabVisible) {
			fabProfileSendMessage.setOnClickListener {
				navController.navigate(R.id.action_profile_to_chatFragment)
			}
		}

	}

	private fun showReportDialog() {
		val materialDialogPicker = MaterialAlertDialogBuilder(context)
			.setItems(arrayOf(getString(R.string.report_chooser_photos),
			                  getString(R.string.report_chooser_behavior),
			                  getString(R.string.report_chooser_fake))) { _, itemIndex ->
				when (itemIndex) {
					0 -> { associatedViewModel.submitReport(Report(INELIGIBLE_PHOTOS,
					                                               selectedUser.baseUserInfo)) }
					1 -> { associatedViewModel.submitReport(Report(DISRESPECTFUL_BEHAVIOR,
					                                               selectedUser.baseUserInfo)) }
					2 -> { associatedViewModel.submitReport(Report(FAKE, selectedUser.baseUserInfo)) }
				}
			}
			.create()
		val params = materialDialogPicker.window?.attributes
		params?.gravity = Gravity.CENTER
		materialDialogPicker.show()
	}

	override fun onBackPressed() {
		navController.navigateUp()
	}

}