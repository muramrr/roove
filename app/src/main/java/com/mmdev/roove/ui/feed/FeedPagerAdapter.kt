package com.mmdev.roove.ui.feed

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmdev.roove.ui.feed.tabitem.FeedPageFragment


/* Created by A on 04.10.2019.*/

/**
 * This is the documentation block about the class
 */

class FeedPagerAdapter (fm: FragmentManager, lifecycle: Lifecycle) :
		FragmentStateAdapter(fm, lifecycle) {


	override fun createFragment(position: Int) = FeedPageFragment()

	override fun getItemCount(): Int = 4

}