/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.01.20 18:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mmdev.roove.R
import com.mmdev.roove.utils.addSystemBottomPadding
import kotlinx.android.synthetic.main.fragment_dating_interactions.*

/**
 * This is the documentation block about the class
 */

class DatingInteractionsFragment: Fragment(R.layout.fragment_dating_interactions) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		bottomNavigationView.addSystemBottomPadding()
		viewPagerDating.isUserInputEnabled = false
		viewPagerDating.adapter = DatingPagerAdapter(childFragmentManager, this.lifecycle)

		bottomNavigationView.setOnNavigationItemSelectedListener {
			when (it.itemId){

				R.id.bottomPairs -> viewPagerDating.currentItem = 1
				R.id.bottomCards -> viewPagerDating.currentItem = 2
				R.id.bottomConversations -> viewPagerDating.currentItem = 3
				R.id.bottomSettings -> viewPagerDating.currentItem = 4
			}

			return@setOnNavigationItemSelectedListener true
		}

	}
}