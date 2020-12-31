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