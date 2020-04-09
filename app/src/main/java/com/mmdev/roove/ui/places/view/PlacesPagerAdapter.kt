/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.04.20 13:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmdev.roove.R
import com.mmdev.roove.ui.places.view.tabitem.PlacesPageFragment


class PlacesPagerAdapter (fm: FragmentManager, lifecycle: Lifecycle) :
		FragmentStateAdapter(fm, lifecycle) {


	override fun createFragment(position: Int) =
		when (position){
			0 -> PlacesPageFragment.newInstance(R.string.places_api_bars)
			1 -> PlacesPageFragment.newInstance(R.string.places_api_entertainment)
			2 -> PlacesPageFragment.newInstance(R.string.places_api_clubs)
			3 -> PlacesPageFragment.newInstance(R.string.places_api_restaurants)
			else -> PlacesPageFragment.newInstance(0)
		}

	override fun getItemCount(): Int = 4

}