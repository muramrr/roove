/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.pairs.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPairsBinding
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.GridItemDecoration
import com.mmdev.roove.ui.pairs.PairsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import dagger.hilt.android.AndroidEntryPoint

/**
 * This is the documentation block about the class
 */

@AndroidEntryPoint
class PairsFragment: BaseFragment<PairsViewModel, FragmentPairsBinding>(
	layoutId = R.layout.fragment_pairs
) {
	
	override val mViewModel: PairsViewModel by viewModels()
	
	private val mPairsAdapter = PairsAdapter()
	
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
		rvPairList.apply {
			adapter = mPairsAdapter
			layoutManager = gridLayoutManager
			addItemDecoration(GridItemDecoration())
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(gridLayoutManager) {
				override fun onLoadMore(page: Int, totalItemsCount: Int) {

					if (gridLayoutManager.findLastCompletelyVisibleItemPosition() <= totalItemsCount - 4){
						mViewModel.loadMoreMatchedUsers()
					}

				}
			})
		}

		mPairsAdapter.setOnItemClickListener { item, position ->
			sharedViewModel.matchedUserItemSelected.value = item
			sharedViewModel.conversationSelected.value = ConversationItem(
				partner = item.baseUserInfo,
				conversationId = item.conversationId,
				conversationStarted = item.conversationStarted
			)
			navController.navigate(R.id.action_pairs_to_profileFragment)
		}
	}

	override fun onResume() {
		super.onResume()
		mViewModel.loadMatchedUsers()
	}
}