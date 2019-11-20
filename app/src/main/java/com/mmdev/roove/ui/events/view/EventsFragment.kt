/*
 * Created by Andrii Kovalchuk on 20.11.19 21:38
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.11.19 21:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.events.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.main.view.MainActivity

class EventsFragment: Fragment(R.layout.fragment_events) {

	private lateinit var mMainActivity: MainActivity

	companion object{
		fun newInstance(): EventsFragment {
			return EventsFragment()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (activity != null) mMainActivity = activity as MainActivity
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val viewPager: ViewPager2 = view.findViewById(R.id.feed_vp)
		viewPager.adapter = EventsPagerAdapter(
				childFragmentManager,
				lifecycle)

		val tabLayout: TabLayout= view.findViewById(R.id.tabLayout)

		TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
			when(position){
				0 -> tab.text = "Popular"
				1 -> tab.text = "Latest"
				2 -> tab.text = "Featured"
				3 -> tab.text = "Friends"
			}
		}.attach()

	}

	override fun onResume() {
		super.onResume()
		mMainActivity.setScrollableToolbar()
		mMainActivity.toolbar.title = "Feed"
	}

	override fun onStop() {
		super.onStop()
		mMainActivity.setNonScrollableToolbar()
	}
}
