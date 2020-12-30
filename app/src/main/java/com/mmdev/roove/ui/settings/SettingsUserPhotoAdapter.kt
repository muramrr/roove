/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.12.20 21:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import com.mmdev.business.core.PhotoItem
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter

/**
 * better to use notifyItemInserted instead of notifyDataSetChanged()
 * but bug with custom layout manager exists
 */

class SettingsUserPhotoAdapter (private var photosUrlsList: List<PhotoItem> = emptyList(),
                                private val layoutId: Int):
		BaseRecyclerAdapter<PhotoItem>(),
		BaseRecyclerAdapter.BindableAdapter<List<PhotoItem>> {

	override fun getItem(position: Int): PhotoItem = photosUrlsList[position]
	override fun getItemCount() = photosUrlsList.size
	override fun getLayoutIdForItem(position: Int) = layoutId

	override fun setData(data: List<PhotoItem>) {
		photosUrlsList = data
		notifyDataSetChanged()
	}
}