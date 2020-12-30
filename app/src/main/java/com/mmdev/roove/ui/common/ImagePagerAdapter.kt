/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.12.20 21:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common

import com.mmdev.roove.R
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter


class ImagePagerAdapter (private var imagesUrlList: List<String> = emptyList(),
						 private val layoutId: Int = R.layout.universal_pager_image_container):
		BaseRecyclerAdapter<String>(),
		BaseRecyclerAdapter.BindableAdapter<List<String>> {

	override fun getItem(position: Int) = imagesUrlList[position]
	override fun getItemCount() = imagesUrlList.size
	override fun getLayoutIdForItem(position: Int) = layoutId

	override fun setData(data: List<String>) {
		imagesUrlList = data
		notifyDataSetChanged()
	}
}