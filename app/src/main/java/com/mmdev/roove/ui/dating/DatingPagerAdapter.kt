/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.01.20 18:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmdev.roove.ui.actions.conversations.view.ConversationsFragment
import com.mmdev.roove.ui.actions.pairs.view.PairsFragment
import com.mmdev.roove.ui.cards.view.CardsFragment
import com.mmdev.roove.ui.settings.SettingsAccountFragment

/**
 * This is the documentation block about the class
 */

class DatingPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
		FragmentStateAdapter(fm, lifecycle) {

	// Returns the fragment to display for that page
	override fun createFragment(position: Int) =
		when (position){
			0 -> ConversationsFragment()
			1 -> PairsFragment()
			2 -> CardsFragment()
			3 -> ConversationsFragment()
			4 -> SettingsAccountFragment()
			else -> CardsFragment()
		}

	override fun getItemCount(): Int = 5


}


