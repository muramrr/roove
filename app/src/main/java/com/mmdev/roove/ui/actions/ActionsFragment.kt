/*
 * Created by Andrii Kovalchuk on 26.11.19 20:29
 * Copyright (c) 2019. All rights reserved.
 * Last modified 26.11.19 17:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.main.view.MainActivity

/**
 * This is the documentation block about the class
 */

class ActionsFragment : Fragment(R.layout.fragment_actions) {

	private lateinit var mMainActivity: MainActivity

	companion object{
		fun newInstance() =  ActionsFragment()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (activity != null) mMainActivity = activity as MainActivity
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val viewPager: ViewPager2 = view.findViewById(R.id.fragment_activities_vp)
		viewPager.adapter = ActionsPagerAdapter(childFragmentManager, lifecycle)

		val tabLayout: TabLayout = view.findViewById(R.id.fragment_activities_tabs)

		TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
			when(position){
				0 -> tab.text = "Chats"
				1 -> tab.text = "Pairs"
			}
		}.attach()

	}

	override fun onResume() {
		super.onResume()
		mMainActivity.setScrollableToolbar()
		mMainActivity.toolbar.title = "Actions"
	}

}
