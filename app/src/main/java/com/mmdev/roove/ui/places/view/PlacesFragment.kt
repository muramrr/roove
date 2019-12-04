/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.MainActivity

class PlacesFragment: Fragment(R.layout.fragment_places) {

	private lateinit var mMainActivity: MainActivity

	companion object{
		fun newInstance(): PlacesFragment {
			return PlacesFragment()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (activity != null) mMainActivity = activity as MainActivity
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val viewPager: ViewPager2 = view.findViewById(R.id.placesViewPager)
		viewPager.apply {
			(getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
			adapter = PlacesPagerAdapter(childFragmentManager, lifecycle)
		}

		val tabLayout: TabLayout= view.findViewById(R.id.tabLayout)

		TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
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
