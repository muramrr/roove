/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.03.20 17:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentMainFlowBinding
import com.mmdev.roove.ui.common.base.FlowFragment
import kotlinx.android.synthetic.main.fragment_main_flow.*

/**
 * This is the documentation block about the class
 */

class MainFlowFragment: FlowFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentMainFlowBinding.inflate(inflater, container, false)
			.apply { executePendingBindings() }
			.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		//set selected bottom menu item on startup
		bottomNavigationView.selectedItemId = R.id.bottomCards
		fabCards.isSelected = true

		val navHost =
			childFragmentManager.findFragmentById(R.id.mainHostFragment) as NavHostFragment
		navController = navHost.findNavController()

		navController.addOnDestinationChangedListener { _, destination, _ ->
			if (destination.id in arrayOf(R.id.chatFragmentNav, R.id.profileFragmentNav)) {
				bottomNavigationView.visibility = View.GONE
			}
			else bottomNavigationView.visibility = View.VISIBLE
		}

		bottomNavigationView.setOnNavigationItemSelectedListener {
			val previousItem = bottomNavigationView.selectedItemId
			val nextItem = it.itemId

			if (previousItem != nextItem) {

				when (nextItem) {
					R.id.bottomPlaces -> {
						navController.popBackStack()
						navController.navigate(R.id.placesFragmentNav)
						fabCards.isSelected = false

					}
					R.id.bottomPairs -> {
						navController.popBackStack()
						navController.navigate(R.id.pairsFragmentNav)
						fabCards.isSelected = false

					}
					//R.id.bottomCards -> navControllerDating.navigate(R.id.cardsFragmentNav)
					R.id.bottomConversations -> {
						navController.popBackStack()
						navController.navigate(R.id.conversationsFragmentNav)
						fabCards.isSelected = false

					}
					R.id.bottomSettings -> {
						navController.popBackStack()
						navController.navigate(R.id.settingsFragmentNav)
						fabCards.isSelected = false

					}
				}
			}

			return@setOnNavigationItemSelectedListener true
		}

		fabCards.setOnClickListener {
			if (navController.currentDestination?.id != R.id.cardsFragmentNav) {
				bottomNavigationView.selectedItemId = R.id.bottomCards
				navController.popBackStack()
				navController.navigate(R.id.cardsFragmentNav)
				it.isSelected = true
			}
		}


	}


}