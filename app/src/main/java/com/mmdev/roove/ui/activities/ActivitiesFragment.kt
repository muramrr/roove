package com.mmdev.roove.ui.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.roove.R
import com.mmdev.roove.ui.main.view.MainActivity

/* Created by A on 13.11.2019.*/

/**
 * This is the documentation block about the class
 */

class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

	private lateinit var mMainActivity: MainActivity

	companion object{
		fun newInstance(): ActivitiesFragment {
			return ActivitiesFragment()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (activity != null) mMainActivity = activity as MainActivity
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val viewPager: ViewPager2 = view.findViewById(R.id.fragment_activities_vp)
		viewPager.adapter = ActivitiesPagerAdapter(childFragmentManager, lifecycle)

		val tabLayout: TabLayout = view.findViewById(R.id.fragment_activities_tabs)

		TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
			when(position){
				0 -> tab.text = "Chats"
				1 -> tab.text = "Pairs"
			}
		}.attach()

	}

	override fun onResume() {
		super.onResume()
		mMainActivity.setScrollableToolbar()
		mMainActivity.toolbar.title = "Activities"
	}

	override fun onStop() {
		super.onStop()
		mMainActivity.setNonScrollableToolbar()
	}
}
