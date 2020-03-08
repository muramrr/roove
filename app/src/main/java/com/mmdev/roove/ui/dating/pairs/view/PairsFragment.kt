/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.03.20 19:27
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPairsBinding
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.common.base.BaseAdapter
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.dating.pairs.PairsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_pairs.*


/**
 * This is the documentation block about the class
 */

class PairsFragment: BaseFragment(R.layout.fragment_pairs) {

	private val mPairsAdapter = PairsAdapter(listOf(), R.layout.fragment_pairs_item)

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var pairsViewModel: PairsViewModel


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		pairsViewModel = ViewModelProvider(this@PairsFragment, factory)[PairsViewModel::class.java]

		pairsViewModel.loadMatchedUsers()

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

					if ((totalItemsCount - visibleItemCount) == (pastVisibleItems + 4)){
						//Log.wtf("mylogs_PairsFragment", "load called ")
						pairsViewModel.loadMoreMatchedUsers()
					}

				}
			})
		}

		mPairsAdapter.setOnItemClickListener(object: BaseAdapter.OnItemClickListener<MatchedUserItem> {
			override fun onItemClick(item: MatchedUserItem, position: Int) {

				sharedViewModel.setMatchedUserItem(item)
				findNavController().navigate(R.id.action_pairs_to_profileFragment)

			}
		})
	}

}