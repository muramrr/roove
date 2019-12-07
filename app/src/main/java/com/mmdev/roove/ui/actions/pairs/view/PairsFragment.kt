/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 07.12.19 20:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions.pairs.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.actions.pairs.PairsViewModel
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.utils.addSystemBottomPadding
import kotlinx.android.synthetic.main.fragment_pairs.*

/**
 * This is the documentation block about the class
 */

class PairsFragment: BaseFragment(R.layout.fragment_pairs) {

	private val mPairsAdapter = PairsAdapter(listOf())


	private lateinit var sharedViewModel: SharedViewModel
	private val factory = injector.factory()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		val pairsViewModel =
			ViewModelProvider(this, factory)[PairsViewModel::class.java]

		pairsViewModel.loadMatchedUsers()

		pairsViewModel.getMatchedUsersList().observe(this, Observer {
			mPairsAdapter.updateData(it)
		})

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		rvPairList.addSystemBottomPadding()

		rvPairList.apply {
			adapter = mPairsAdapter
			layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
			itemAnimator = DefaultItemAnimator()
		}

		mPairsAdapter.setOnItemClickListener(object: PairsAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {

				sharedViewModel.setCardSelected(mPairsAdapter.getPairItem(position))

				findNavController().navigate(R.id.action_nav_actions_pairs_to_profileFragment)

			}
		})
	}



}