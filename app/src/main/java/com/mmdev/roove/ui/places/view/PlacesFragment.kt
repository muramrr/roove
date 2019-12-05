/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 05.12.19 16:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_places.*

class PlacesFragment: BaseFragment(R.layout.fragment_places) {


	companion object{
		fun newInstance() = PlacesFragment()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		viewPagerPlaces.apply {
			(getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
			adapter = PlacesPagerAdapter(childFragmentManager, lifecycle)
		}

		TabLayoutMediator(tabLayoutPlaces, viewPagerPlaces) { tab: TabLayout.Tab, position: Int ->
			when (position){
				0 -> tab.text = "Bars"
				1 -> tab.text = "Restaurants"
				2 -> tab.text = "Clubs"
				3 -> tab.text = "Museums"
			}
		}.attach()

	}

//	override fun onResume() {
//		super.onResume()
//		mMainActivity.setScrollableToolbar()
//		mMainActivity.toolbar.title = "Places"
//	}
//
//	override fun onStop() {
//		super.onStop()
//		mMainActivity.setNonScrollableToolbar()
//	}
}
