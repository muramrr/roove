/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.12.20 21:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.conversations.view

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
import com.mmdev.roove.databinding.FragmentConversationsBinding
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter
import com.mmdev.roove.ui.common.custom.SwipeToDeleteCallback
import com.mmdev.roove.ui.dating.conversations.ConversationsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_conversations.*

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: BaseFragment<ConversationsViewModel>(){

	private val mConversationsAdapter = ConversationsAdapter(layoutId = R.layout.fragment_conversations_item)

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