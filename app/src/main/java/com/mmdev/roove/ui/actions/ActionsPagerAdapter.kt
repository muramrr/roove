package com.mmdev.roove.ui.actions

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmdev.roove.ui.actions.conversations.view.ConversationsFragment
import com.mmdev.roove.ui.actions.pairs.view.PairsFragment

/* Created by A on 13.11.2019.*/

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


