/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 05.12.19 16:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_actions.*

/**
 * This is the documentation block about the class
 */

class ActionsFragment : BaseFragment(R.layout.fragment_actions) {

	companion object{
		fun newInstance() =  ActionsFragment()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		viewPagerActivities.adapter = ActionsPagerAdapter(childFragmentManager, lifecycle)

		TabLayoutMediator(tabsActivities, viewPagerActivities) { tab: TabLayout.Tab, position: Int ->
			when(position){
				0 -> tab.text = "Chats"
				1 -> tab.text = "Pairs"
			}
		}.attach()

	}

}
