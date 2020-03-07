/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 19:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.pairs.view

import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.roove.ui.common.base.BaseAdapter


class PairsAdapter (private var mPairsList: List<MatchedUserItem>,
                    private val layoutId: Int):
		BaseAdapter<MatchedUserItem>(),
		BaseAdapter.BindableAdapter<List<MatchedUserItem>> {

	override fun getItem(position: Int) = mPairsList[position]
	override fun getItemCount() = mPairsList.size
	override fun getLayoutIdForItem(position: Int) = layoutId

	override fun setData(data: List<MatchedUserItem>) {
		mPairsList = data
		notifyDataSetChanged()
	}
}