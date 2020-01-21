/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.01.20 19:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.FlowFragment
import com.mmdev.roove.utils.addSystemBottomPadding
import com.mmdev.roove.utils.addSystemTopPadding
import kotlinx.android.synthetic.main.fragment_main_flow.*

/**
 * This is the documentation block about the class
 */

class MainFlowFragment: FlowFragment(R.layout.fragment_main_flow) {

	private lateinit var navControllerMain: NavController

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		containerMain.addSystemTopPadding()
		bottomNavigationView.addSystemBottomPadding()
		bottomNavigationView.selectedItemId = R.id.bottomCards


		val navHost =
			childFragmentManager.findFragmentById(R.id.mainHostFragment) as NavHostFragment
		navControllerMain = navHost.findNavController()

		navControllerMain.addOnDestinationChangedListener { _, destination, _ ->
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
						navControllerMain.popBackStack()
						navControllerMain.navigate(R.id.placesFragmentNav)

					}
					R.id.bottomPairs -> {
						navControllerMain.popBackStack()
						navControllerMain.navigate(R.id.pairsFragmentNav)

					}
					//R.id.bottomCards -> navControllerDating.navigate(R.id.cardsFragmentNav)
					R.id.bottomConversations -> {
						navControllerMain.popBackStack()
						navControllerMain.navigate(R.id.conversationsFragmentNav)

					}
					R.id.bottomSettings -> {
						navControllerMain.popBackStack()
						navControllerMain.navigate(R.id.settingsFragmentNav)

					}
				}
			}

			return@setOnNavigationItemSelectedListener true
		}

		fabCards.setOnClickListener {
			if (navControllerMain.currentDestination?.id != R.id.cardsFragmentNav) {
				bottomNavigationView.selectedItemId = R.id.bottomCards
				navControllerMain.popBackStack()
				navControllerMain.navigate(R.id.cardsFragmentNav)
			}
		}


	}


}