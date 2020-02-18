/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.02.20 20:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.pairs.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPairsBinding
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.dating.pairs.PairsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_pairs.*


/**
 * This is the documentation block about the class
 */

class PairsFragment: BaseFragment(R.layout.fragment_pairs) {

	private val mPairsAdapter = PairsAdapter(listOf())

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var pairsViewModel: PairsViewModel


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		pairsViewModel = ViewModelProvider(this@PairsFragment, factory)[PairsViewModel::class.java]

		pairsViewModel.getMatchedUsersList().observe(this, Observer {
			mPairsAdapter.updateData(it)
		})

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentPairsBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@PairsFragment
				viewModel = pairsViewModel
				executePendingBindings()
			}
			.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
		rvPairList.apply {
			adapter = mPairsAdapter
			layoutManager = staggeredGridLayoutManager
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
				override fun onLoadMore(page: Int, totalItemsCount: Int) {
					val visibleItemCount = staggeredGridLayoutManager.childCount

					val pastVisibleItems = staggeredGridLayoutManager
						.findLastVisibleItemPositions(null)[0]

					if ((totalItemsCount - visibleItemCount) <= (pastVisibleItems + 4)){
						//Log.wtf("mylogs_PairsFragment", "load called ")
						pairsViewModel.loadMatchedUsers()
					}

				}
			})
		}

		mPairsAdapter.setOnItemClickListener(object: PairsAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {

				sharedViewModel.setMatchedUserItem(mPairsAdapter.getPairItem(position))

				findNavController().navigate(R.id.action_pairs_to_profileFragment)

			}
		})
	}

	override fun onResume() {
		super.onResume()
		pairsViewModel.loadMatchedUsers()
	}
}