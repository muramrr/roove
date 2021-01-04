/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentConversationsBinding
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.SwipeToDeleteCallback
import com.mmdev.roove.ui.conversations.ConversationsViewModel
import com.mmdev.roove.utils.extensions.showToastText
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for displaying active conversations
 * //todo: load more conversations on scroll
 */

@AndroidEntryPoint
class ConversationsFragment: BaseFragment<ConversationsViewModel, FragmentConversationsBinding>(
	layoutId = R.layout.fragment_conversations
){
	
	override val mViewModel: ConversationsViewModel by viewModels()

	private val mConversationsAdapter = ConversationsAdapter()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		mViewModel.getDeleteConversationStatus().observe(this, {
			if (it) requireContext().showToastText(getString(R.string.toast_text_delete_success))
		})
		
		mViewModel.conversationsList.observe(this, {
			mConversationsAdapter.setNewData(it)
		})
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		rvConversationList.apply {
			setHasFixedSize(true)
			
			adapter = mConversationsAdapter
			layoutManager = LinearLayoutManager(context)
			addItemDecoration(DividerItemDecoration(this.context, VERTICAL))

			val swipeHandler = object : SwipeToDeleteCallback(context) {
				override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

					val adapter = rvConversationList.adapter as ConversationsAdapter
					val itemPosition = viewHolder.adapterPosition

					MaterialAlertDialogBuilder(context)
						.setCancelable(false)
						.setTitle(getString(R.string.dialog_conversation_delete_title))
						.setMessage(getString(R.string.dialog_conversation_delete_message))
						.setPositiveButton(getString(R.string.dialog_delete_btn_positive_text)) { dialog, _ ->
							mViewModel.deleteConversation(adapter.getItem(itemPosition))
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

		mConversationsAdapter.setOnItemClickListener { item, position ->
			
			sharedViewModel.conversationSelected.value = item
			sharedViewModel.matchedUserItemSelected.value = MatchedUserItem(
				baseUserInfo = item.partner,
				conversationId = item.conversationId,
				conversationStarted = item.conversationStarted
			)
			navController.navigate(R.id.action_conversations_to_chatFragment)
			
		}

	}

}