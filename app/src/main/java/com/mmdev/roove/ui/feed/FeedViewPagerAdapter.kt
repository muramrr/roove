package com.mmdev.roove.ui.feed

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmdev.roove.ui.feed.tabitem.FeedPageFragment


/* Created by A on 04.10.2019.*/

/**
 * This is the documentation block about the class
 */

class FeedViewPagerAdapter (fm: FragmentManager, lifecycle: Lifecycle) :
		FragmentStateAdapter(fm, lifecycle) {


	override fun createFragment(position: Int) = FeedPageFragment()


	// Returns the fragment to display for that page
	fun getItem(position: Int): Fragment? {
		return when (position) {
			0 -> FeedPageFragment()

			1 -> FeedPageFragment()

			2 -> FeedPageFragment()

			3 -> FeedPageFragment()

			else -> null
		}
	}

	override fun getItemCount(): Int = 4



}