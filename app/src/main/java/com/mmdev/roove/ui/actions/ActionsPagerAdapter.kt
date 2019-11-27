/*
 * Created by Andrii Kovalchuk on 27.11.19 19:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 27.11.19 19:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmdev.roove.ui.actions.conversations.view.ConversationsFragment
import com.mmdev.roove.ui.actions.pairs.view.PairsFragment

/**
 * This is the documentation block about the class
 */

class ActionsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
		FragmentStateAdapter(fm, lifecycle) {

	// Returns the fragment to display for that page
	override fun createFragment(position: Int) =
		if (position == 0) ConversationsFragment()
		else PairsFragment()

	override fun getItemCount(): Int = 2


}


