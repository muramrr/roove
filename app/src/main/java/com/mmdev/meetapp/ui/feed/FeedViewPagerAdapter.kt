package com.mmdev.meetapp.ui.feed

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter



/* Created by A on 04.10.2019.*/

/**
 * This is the documentation block about the class
 */

class FeedViewPagerAdapter constructor(fm: FragmentManager,
                                       lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {



	override fun createFragment(position: Int): Fragment {
		return FeedFragmentPage()
	}



	// Returns the fragment to display for that page
	fun getItem(position: Int): Fragment? {
		return when (position) {
			0 -> FeedFragmentPage()

			1 -> FeedFragmentPage()

			2 -> FeedFragmentPage()

			3 -> FeedFragmentPage()

			else -> null
		}
	}

	override fun getItemCount(): Int = 4



}

