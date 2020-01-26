/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.01.20 14:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.detailed


import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.business.base.BasePlaceInfo
import com.mmdev.business.places.PlaceDetailedItem
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.ImagePagerAdapter
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.core.viewmodel.LocalUserRepoViewModel
import com.mmdev.roove.ui.core.viewmodel.RemoteUserRepoViewModel
import com.mmdev.roove.ui.places.PlacesViewModel
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.fragment_place_detailed.*


/**
 * A simple [Fragment] subclass.
 */
class PlaceDetailedFragment: BaseFragment(R.layout.fragment_place_detailed) {

	private lateinit var userItem: UserItem

	private val placePhotosAdapter = ImagePagerAdapter(listOf())

	private var receivedPlaceId = 0

	private lateinit var placeDetailedItem: PlaceDetailedItem

	private lateinit var placesViewModel: PlacesViewModel
	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var localRepoViewModel: LocalUserRepoViewModel
	private lateinit var remoteRepoViewModel: RemoteUserRepoViewModel

	companion object{
		private const val PLACE_ID_KEY = "PLACE_ID"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		arguments?.let {
			receivedPlaceId = it.getInt(PLACE_ID_KEY)
		}

		activity?.run {
			localRepoViewModel = ViewModelProvider(this, factory)[LocalUserRepoViewModel::class.java]
			remoteRepoViewModel= ViewModelProvider(this, factory)[RemoteUserRepoViewModel::class.java]
			sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		placesViewModel = ViewModelProvider(this, factory)[PlacesViewModel::class.java]

		placesViewModel.loadPlaceDetails(receivedPlaceId)

		placesViewModel.getPlaceDetailed().observeOnce(this, Observer {
			placeDetailedItem = it
			val placePhotos = ArrayList<String>()
			for (imageItem in it.images)
				placePhotos.add(imageItem.image)


			placePhotosAdapter.updateData(placePhotos)

			collapseBarPlaceDetailed.title = it.short_title

			tvPlaceDetailedDescription.text = it.description
			tvPlaceDetailedFullDescription.text = it.body_text
		})
		sharedViewModel.getCurrentUser().value?.let { userItem = it }
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		toolbarPlaceDetailed.setNavigationOnClickListener { findNavController().navigateUp() }

		viewPagerPlaceDetailedPhotos.apply {
			(getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
			adapter = placePhotosAdapter
		}

		TabLayoutMediator(tlDotsIndicatorPlace, viewPagerPlaceDetailedPhotos){
			_: TabLayout.Tab, _: Int -> //do nothing
		}.attach()


		ibExpandDescriptionPlaceDetailed.setOnClickListener {
			cycleTextViewExpansion(tvPlaceDetailedFullDescription)
		}

		tvPlaceDetailedFullDescription.setOnClickListener {
			cycleTextViewExpansion(tvPlaceDetailedFullDescription)
		}

		fabPlaceDetailed.setOnClickListener {
			val placeToGoItem = BasePlaceInfo(placeDetailedItem.id,
			                                  placeDetailedItem.short_title,
			                                  placeDetailedItem.images[0].image)

			if (!userItem.placesToGo.contains(placeToGoItem)){
				userItem.placesToGo.add(placeToGoItem)
				remoteRepoViewModel.updateUserItem(userItem)
				remoteRepoViewModel.getUserUpdateStatus().observeOnce(this, Observer {
					if (it) localRepoViewModel.saveUserInfo(userItem)
				})
			}
			//Log.wtf("mylogs", "{${userItem.placesToGo}}")
		}
	}


	private fun cycleTextViewExpansion(tv: TextView) {
		val collapsedMaxLines = 1
		val animation = ObjectAnimator.ofInt(tv,
		                                     "maxLines",
		                                     if (tv.maxLines == collapsedMaxLines) tv.lineCount
		                                     else collapsedMaxLines)
		animation.setDuration(100).start()
		if (tv.maxLines == collapsedMaxLines)
			ibExpandDescriptionPlaceDetailed.setImageResource(R.drawable.ic_arrow_drop_up_24dp)
		else ibExpandDescriptionPlaceDetailed.setImageResource(R.drawable.ic_arrow_drop_down_24dp)
	}

	override fun onBackPressed() {
		findNavController().navigateUp()
	}


}
