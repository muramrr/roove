/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.04.20 18:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile.view

import com.mmdev.business.places.BasePlaceInfo
import com.mmdev.roove.R
import com.mmdev.roove.ui.common.base.BaseAdapter


class PlacesToGoAdapter (private var mPlacesToGoList: List<BasePlaceInfo> = emptyList(),
                         private val layoutId: Int = R.layout.fragment_profile_places_rv_item):
		BaseAdapter<BasePlaceInfo>(),
		BaseAdapter.BindableAdapter<List<BasePlaceInfo>> {

	override fun getItem(position: Int): BasePlaceInfo = mPlacesToGoList[position]
	override fun getItemCount() = mPlacesToGoList.size
	override fun getLayoutIdForItem(position: Int): Int = layoutId

	override fun setData(data: List<BasePlaceInfo>) {
		mPlacesToGoList = data
		notifyDataSetChanged()
	}
}