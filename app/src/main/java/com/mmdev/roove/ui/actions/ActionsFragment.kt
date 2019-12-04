/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.core.BaseFragment

/**
 * This is the documentation block about the class
 */

class ActionsFragment : BaseFragment(R.layout.fragment_actions) {

	private lateinit var mMainActivity: MainActivity

	companion object{
		fun newInstance() =  ActionsFragment()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = activity as MainActivity }
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

}
