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

package com.mmdev.roove.ui.profile.view

import com.mmdev.business.places.BasePlaceInfo
import com.mmdev.roove.R
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter


class PlacesToGoAdapter (private var mPlacesToGoList: List<BasePlaceInfo> = emptyList(),
                         private val layoutId: Int = R.layout.fragment_profile_places_rv_item):
		BaseRecyclerAdapter<BasePlaceInfo>(),
		BaseRecyclerAdapter.BindableAdapter<List<BasePlaceInfo>> {

	override fun getItem(position: Int): BasePlaceInfo = mPlacesToGoList[position]
	override fun getItemCount() = mPlacesToGoList.size
	override fun getLayoutIdForItem(position: Int): Int = layoutId

	override fun setData(data: List<BasePlaceInfo>) {
		mPlacesToGoList = data
		notifyDataSetChanged()
	}
}