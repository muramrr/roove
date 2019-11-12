package com.mmdev.roove.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.main.view.MainActivity

class FeedFragment: Fragment(R.layout.fragment_feed) {

	private lateinit var mMainActivity: MainActivity

	companion object{
		fun newInstance(): FeedFragment {
			return FeedFragment()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (activity != null) mMainActivity = activity as MainActivity
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val viewPager: ViewPager2 = view.findViewById(R.id.feed_vp)
		viewPager.adapter = FeedViewPagerAdapter(childFragmentManager, lifecycle)

		val tabLayout: TabLayout= view.findViewById(R.id.tabLayout)

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
