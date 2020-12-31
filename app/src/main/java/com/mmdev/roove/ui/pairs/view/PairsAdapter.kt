/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.pairs.view

import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter


class PairsAdapter(
	private var mPairsList: List<MatchedUserItem> = emptyList()
): BaseRecyclerAdapter<MatchedUserItem>(),
   BaseRecyclerAdapter.BindableAdapter<List<MatchedUserItem>> {

	override fun getItem(position: Int) = mPairsList[position]
	override fun getItemCount() = mPairsList.size
	override fun getLayoutIdForItem(position: Int) = R.layout.fragment_pairs_item

	override fun setData(data: List<MatchedUserItem>) {
		mPairsList = data
		notifyDataSetChanged()
	}
}