/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.conversations.view

import com.mmdev.business.conversations.ConversationItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter

class ConversationsAdapter(
	private var conversationsList: MutableList<ConversationItem> = mutableListOf()
): BaseRecyclerAdapter<ConversationItem>(),
   BaseRecyclerAdapter.BindableAdapter<MutableList<ConversationItem>> {

	override fun getItem(position: Int) = conversationsList[position]
	override fun getItemCount() = conversationsList.size
	override fun getLayoutIdForItem(position: Int) = R.layout.fragment_conversations_item

	override fun setData(data: MutableList<ConversationItem>) {
		conversationsList = data
		notifyDataSetChanged()
	}

	fun removeAt(position: Int) {
		conversationsList.removeAt(position)
		notifyItemRemoved(position)
	}
}