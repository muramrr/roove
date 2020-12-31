/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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