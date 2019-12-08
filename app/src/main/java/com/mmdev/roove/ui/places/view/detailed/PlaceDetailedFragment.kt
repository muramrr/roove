/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 08.12.19 21:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.detailed


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.ImagePagerAdapter
import com.mmdev.roove.ui.core.SharedViewModel
import kotlinx.android.synthetic.main.fragment_place_detailed.*

/**
 * A simple [Fragment] subclass.
 */
class PlaceDetailedFragment: BaseFragment(R.layout.fragment_place_detailed) {

	private val placePhotosAdapter = ImagePagerAdapter(listOf())

	private lateinit var sharedViewModel: SharedViewModel


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		sharedViewModel.placeSelected.observe(this, Observer {
			val placePhotos = ArrayList<String>()
			for (imageItem in it.images)
				placePhotos.add(imageItem.image)

			placePhotosAdapter.updateData(placePhotos)

			tvPlaceTitle.text = it.short_title

			tvPlaceAboutText.text = it.description
				.replace("<p>", "")
				.replace("</p>","")

		})


	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		viewPagerPlacePhotos.apply {
			(getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
			adapter = placePhotosAdapter
		}

		TabLayoutMediator(tlDotsIndicatorPlace, viewPagerPlacePhotos){
			_: TabLayout.Tab, _: Int ->
			//do nothing
		}.attach()


	}


}
