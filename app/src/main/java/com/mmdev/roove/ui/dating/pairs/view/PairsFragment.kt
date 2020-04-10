/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.04.20 16:53
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
import androidx.recyclerview.widget.GridLayoutManager
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPairsBinding
import com.mmdev.roove.ui.common.base.BaseAdapter
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.GridItemDecoration
import com.mmdev.roove.ui.dating.pairs.PairsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_pairs.*


/**
 * This is the documentation block about the class
 */

class PairsFragment: BaseFragment<PairsViewModel>() {

	private val mPairsAdapter = PairsAdapter(layoutId = R.layout.fragment_pairs_item)


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
		val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
		rvPairList.apply {
			adapter = mPairsAdapter
			layoutManager = gridLayoutManager
			addItemDecoration(GridItemDecoration())
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(gridLayoutManager) {
				override fun onLoadMore(page: Int, totalItemsCount: Int) {

					if (gridLayoutManager.findLastCompletelyVisibleItemPosition() <= totalItemsCount - 4){
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