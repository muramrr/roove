/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.12.20 21:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.tabitem

import com.mmdev.business.places.PlaceItem
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter


class PlacesRecyclerAdapter (private var mPlaceList: List<PlaceItem> = emptyList(),
                             private val layoutId: Int):
		BaseRecyclerAdapter<PlaceItem>(),
		BaseRecyclerAdapter.BindableAdapter<List<PlaceItem>>{

	override fun getItem(position: Int) = mPlaceList[position]
	override fun getItemCount() = mPlaceList.size
	override fun getLayoutIdForItem(position: Int) = layoutId

	override fun setData(data: List<PlaceItem>) {
		mPlaceList = data
		notifyDataSetChanged()
	}
}
