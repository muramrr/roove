/*
 * Created by Andrii Kovalchuk on 22.11.19 19:36
 * Copyright (c) 2019. All rights reserved.
 * Last modified 22.11.19 16:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.main.view.MainActivity

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
		val viewPager: ViewPager2 = view.findViewById(R.id.feed_vp)
		viewPager.adapter = PlacesPagerAdapter(
				childFragmentManager,
				lifecycle)

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

	override fun onResume() {
		super.onResume()
		mMainActivity.setScrollableToolbar()
		mMainActivity.toolbar.title = "Places"
	}

	override fun onStop() {
		super.onStop()
		mMainActivity.setNonScrollableToolbar()
	}
}
