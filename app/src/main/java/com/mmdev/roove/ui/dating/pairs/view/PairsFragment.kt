/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.04.20 17:30
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPairsBinding
import com.mmdev.roove.ui.common.base.BaseAdapter
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.dating.pairs.PairsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_pairs.*


/**
 * This is the documentation block about the class
 */

class PairsFragment: BaseFragment<PairsViewModel>() {

	private val mPairsAdapter = PairsAdapter(listOf(), R.layout.fragment_pairs_item)


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		associatedViewModel = getViewModel()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentPairsBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@PairsFragment
				viewModel = associatedViewModel
				executePendingBindings()
			}.root

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
					//Log.wtf(TAG, "past visible items = $pastVisibleItems")
					//Log.wtf(TAG, "totalitems = $totalItemsCount")
					//Log.wtf(TAG, "visible items = $visibleItemCount")
					if ((totalItemsCount - visibleItemCount) <= (pastVisibleItems + 4)){
						//Log.wtf(TAG, "load called ")
						associatedViewModel.loadMoreMatchedUsers()
					}

				}
			})
		}

		mPairsAdapter.setOnItemClickListener(object: BaseAdapter.OnItemClickListener<MatchedUserItem> {
			override fun onItemClick(item: MatchedUserItem, position: Int) {

				sharedViewModel.matchedUserItemSelected.value = item
				sharedViewModel.conversationSelected.value =
					ConversationItem(partner = item.baseUserInfo,
					                 conversationId = item.conversationId,
					                 conversationStarted = item.conversationStarted)
				navController.navigate(R.id.action_pairs_to_profileFragment)
			}
		})
	}

	override fun onResume() {
		super.onResume()
		associatedViewModel.loadMatchedUsers()
	}
}