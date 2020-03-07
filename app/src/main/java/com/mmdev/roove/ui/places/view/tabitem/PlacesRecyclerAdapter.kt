/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 17:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.tabitem

import com.mmdev.business.places.PlaceItem
import com.mmdev.roove.ui.common.base.BaseAdapter


class PlacesRecyclerAdapter (private var mPlaceList: List<PlaceItem>,
                             private val layoutId: Int):
		BaseAdapter<PlaceItem>(),
		BaseAdapter.BindableAdapter<List<PlaceItem>>{

	override fun getItem(position: Int) = mPlaceList[position]
	override fun getItemCount() = mPlaceList.size
	override fun getLayoutIdForItem(position: Int) = layoutId

	override fun setData(data: List<PlaceItem>) {
		mPlaceList = data
		notifyDataSetChanged()
	}
}
