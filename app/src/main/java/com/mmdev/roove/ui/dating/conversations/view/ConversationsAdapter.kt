/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.04.20 13:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.conversations.view

import com.mmdev.business.conversations.ConversationItem
import com.mmdev.roove.ui.common.base.BaseAdapter


class ConversationsAdapter (private var conversationsList: MutableList<ConversationItem> = mutableListOf(),
                            private val layoutId: Int):
		BaseAdapter<ConversationItem>(),
		BaseAdapter.BindableAdapter<MutableList<ConversationItem>> {

	override fun getItem(position: Int) = conversationsList[position]
	override fun getItemCount() = conversationsList.size
	override fun getLayoutIdForItem(position: Int) = layoutId

	override fun setData(data: MutableList<ConversationItem>) {
		conversationsList = data
		notifyDataSetChanged()
	}

	fun removeAt(position: Int) {
		conversationsList.removeAt(position)
		notifyItemRemoved(position)
	}
}