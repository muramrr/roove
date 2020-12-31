/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentMainFlowBinding
import com.mmdev.roove.ui.common.base.FlowFragment

/**
 * Main flow fragment, user will access it when auth was successful
 */

class MainFlowFragment: FlowFragment<Nothing, FragmentMainFlowBinding>(
	layoutId = R.layout.fragment_main_flow
) {
	
	override val mViewModel: Nothing? = null

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		//set selected bottom menu item on startup
		binding.bottomNavigationView.selectedItemId = R.id.bottomCards

		val navHost =
			childFragmentManager.findFragmentById(R.id.mainHostFragment) as NavHostFragment
		navController = navHost.findNavController()

		navController.addOnDestinationChangedListener { _, destination, _ ->
			if (destination.id in arrayOf(
						R.id.chatFragmentNav,
						R.id.profileFragmentNav,
						R.id.settingsEditInfoFragmentNav
					)
			) {

				binding.containerBottomNavigation.visibility = View.GONE
			}
			else binding.containerBottomNavigation.visibility = View.VISIBLE
		}
		
		setupBottomNavigation()
	}
	
	private fun setupBottomNavigation() = binding.bottomNavigationView.run {
		setOnNavigationItemSelectedListener {
			val previousItem = selectedItemId
			val nextItem = it.itemId
			
			if (previousItem != nextItem) {
				
				when (nextItem) {
					R.id.bottomPairs -> {
						navController.popBackStack()
						navController.navigate(R.id.pairsFragmentNav)
					}
					R.id.bottomCards -> {
						navController.popBackStack()
						navController.navigate(R.id.cardsFragmentNav)
					}
					R.id.bottomConversations -> {
						navController.popBackStack()
						navController.navigate(R.id.conversationsFragmentNav)
					}
					R.id.bottomSettings -> {
						navController.popBackStack()
						navController.navigate(R.id.settingsFragmentNav)
					}
				}
			}
			
			return@setOnNavigationItemSelectedListener true
		}
	}


}