/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 18:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.cards.view

import com.mmdev.business.core.UserItem
import com.mmdev.roove.ui.common.base.BaseAdapter


class CardsStackAdapter (private var usersList: List<UserItem>,
                         private val layoutId: Int):
		BaseAdapter<UserItem>(),
		BaseAdapter.BindableAdapter<List<UserItem>> {

	override fun getItem(position: Int) = usersList[position]
	override fun getItemCount() = usersList.size
	override fun getLayoutIdForItem(position: Int) = layoutId

	override fun setData(data: List<UserItem>) {
		usersList = data
		notifyDataSetChanged()
	}
}
