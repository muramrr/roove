/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter
import com.mmdev.roove.ui.common.custom.GridItemDecoration
import com.mmdev.roove.ui.pairs.PairsViewModel
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

		mPairsAdapter.setOnItemClickListener(object: BaseRecyclerAdapter.OnItemClickListener<MatchedUserItem> {
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