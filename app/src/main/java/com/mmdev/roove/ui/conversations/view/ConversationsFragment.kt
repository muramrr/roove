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

package com.mmdev.roove.ui.conversations.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.roove.R
import com.mmdev.roove.R.layout
import com.mmdev.roove.databinding.FragmentConversationsBinding
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter
import com.mmdev.roove.ui.common.custom.SwipeToDeleteCallback
import com.mmdev.roove.ui.conversations.ConversationsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_conversations.*

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: BaseFragment<ConversationsViewModel>(){

	private val mConversationsAdapter = ConversationsAdapter(layoutId = layout.fragment_conversations_item)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		associatedViewModel = getViewModel()

		associatedViewModel.getDeleteConversationStatus().observe(this, Observer {
			if (it) context.showToastText(getString(R.string.toast_text_delete_success))
		})

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentConversationsBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@ConversationsFragment
				viewModel = associatedViewModel
				executePendingBindings()
			}.root



	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val linearLayoutManager = LinearLayoutManager(context, VERTICAL, false)
		rvConversationList.apply {
			adapter = mConversationsAdapter
			layoutManager = linearLayoutManager
			addItemDecoration(DividerItemDecoration(this.context, VERTICAL))

			//load more conversations on scroll
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(linearLayoutManager) {
				override fun onLoadMore(page: Int, totalItemsCount: Int) {

					if (linearLayoutManager.findLastCompletelyVisibleItemPosition() <= totalItemsCount - 4){
						//Log.wtf(TAG, "load seems to be called")
						associatedViewModel.loadMoreConversations()
					}
				}
			})

			val swipeHandler = object : SwipeToDeleteCallback(context) {
				override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

					val adapter = rvConversationList.adapter as ConversationsAdapter
					val itemPosition = viewHolder.adapterPosition

					MaterialAlertDialogBuilder(context)
						.setCancelable(false)
						.setTitle(getString(R.string.dialog_conversation_delete_title))
						.setMessage(getString(R.string.dialog_conversation_delete_message))
						.setPositiveButton(getString(R.string.dialog_delete_btn_positive_text)) { dialog, _ ->
							associatedViewModel.deleteConversation(adapter.getItem(itemPosition))
							adapter.removeAt(itemPosition)
							dialog.dismiss()
						}
						.setNegativeButton(getString(R.string.dialog_delete_btn_negative_text)) { dialog, _ ->
							adapter.notifyItemChanged(itemPosition)
							dialog.dismiss()
						}
						.show()
				}
			}
			val itemTouchHelper = ItemTouchHelper(swipeHandler)
			itemTouchHelper.attachToRecyclerView(this)

		}

		mConversationsAdapter.setOnItemClickListener(object: BaseRecyclerAdapter.OnItemClickListener<ConversationItem> {
			override fun onItemClick(item: ConversationItem, position: Int) {

				sharedViewModel.conversationSelected.value = item
				sharedViewModel.matchedUserItemSelected.value =
					MatchedUserItem(baseUserInfo = item.partner,
					                conversationId = item.conversationId,
					                conversationStarted = item.conversationStarted)
				navController.navigate(R.id.action_conversations_to_chatFragment)
			}
		})

	}


	override fun onResume() {
		super.onResume()
		associatedViewModel.loadConversationsList()
	}
}