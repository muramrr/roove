package com.mmdev.meetapp.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.meetapp.R
import com.mmdev.meetapp.ui.main.view.MainActivity

class FeedFragment: Fragment(R.layout.fragment_feed) {

	private lateinit var mMainActivity: MainActivity
	private lateinit var viewPagerAdapter: FeedViewPagerAdapter
	private lateinit var tabLayout: TabLayout
	private lateinit var viewPager: ViewPager2

	companion object{
		fun newInstance(): FeedFragment {
			return FeedFragment()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		if (activity != null) mMainActivity = activity as MainActivity
		viewPager = view.findViewById(R.id.viewPager)
		viewPagerAdapter = FeedViewPagerAdapter(childFragmentManager, lifecycle)
		viewPager.adapter = viewPagerAdapter
		tabLayout = view.findViewById(R.id.tabLayout)

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
