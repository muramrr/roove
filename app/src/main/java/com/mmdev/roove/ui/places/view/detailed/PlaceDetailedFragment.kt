/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.04.20 14:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.detailed

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.business.core.UserItem
import com.mmdev.business.places.BasePlaceInfo
import com.mmdev.business.places.PlaceDetailedItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPlaceDetailedBinding
import com.mmdev.roove.ui.common.ImagePagerAdapter
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.places.PlacesViewModel
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.fragment_place_detailed.*


class PlaceDetailedFragment: BaseFragment<PlacesViewModel>() {

	private lateinit var currentUser: UserItem

	private val placePhotosAdapter = ImagePagerAdapter()

	private var receivedPlaceId = 0

	private lateinit var placeBaseInfo: BasePlaceInfo
	private lateinit var placeDetailedItem: PlaceDetailedItem

	companion object {
		private const val PLACE_ID_KEY = "PLACE_ID"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		associatedViewModel = getViewModel()

		arguments?.let {
			receivedPlaceId = it.getInt(PLACE_ID_KEY)
		}

		sharedViewModel.getCurrentUser().observeOnce(this, Observer {
			currentUser = it
			associatedViewModel.isAddedToProfile.value =
				it.placesToGo.map { place -> place.id }.contains(receivedPlaceId)
		})

		associatedViewModel.loadPlaceDetails(receivedPlaceId)

		associatedViewModel.placeDetailed.observeOnce(this, Observer {
			placeDetailedItem = it
			val placePhotos = it.images.map { imageItem -> imageItem.image }
			placePhotosAdapter.setData(placePhotos)
			placeBaseInfo = BasePlaceInfo(placeDetailedItem.id,
			                              placeDetailedItem.short_title,
			                              placeDetailedItem.images[0].image)
		})
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentPlaceDetailedBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@PlaceDetailedFragment
				viewModel = associatedViewModel
				executePendingBindings()
			}.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		toolbarPlaceDetailed.setNavigationOnClickListener { navController.navigateUp() }

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

		fabAddPlaceToWantToGoList.setOnClickListener {
			if (!currentUser.placesToGo.contains(placeBaseInfo))
				associatedViewModel.addPlaceToProfile(placeBaseInfo)
		}

		fabRemovePlaceFromWantToGoList.setOnClickListener {

			associatedViewModel.removePlaceFromProfile(placeBaseInfo)

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
		navController.navigateUp()
	}


}
