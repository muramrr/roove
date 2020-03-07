/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 18:13
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentConversationsBinding
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.common.base.BaseAdapter
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.SwipeToDeleteCallback
import com.mmdev.roove.ui.dating.conversations.ConversationsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.fragment_conversations.*

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: BaseFragment(R.layout.fragment_conversations){

	private val mConversationsAdapter =
		ConversationsAdapter(mutableListOf(), R.layout.fragment_conversations_item)

	private lateinit var snackbar: Snackbar

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var conversationsViewModel: ConversationsViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		conversationsViewModel = ViewModelProvider(this@ConversationsFragment, factory)[ConversationsViewModel::class.java]
		conversationsViewModel.loadConversationsList()

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		val rootView = FragmentConversationsBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@ConversationsFragment
				viewModel = conversationsViewModel
				executePendingBindings()
			}
			.root
		snackbar = makeSnackbar(rootView)
		return rootView
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val linearLayoutManager = LinearLayoutManager(context, VERTICAL, false)
		rvConversationList.apply {
			adapter = mConversationsAdapter
			layoutManager = linearLayoutManager
			addItemDecoration(DividerItemDecoration(this.context, VERTICAL))

			//load more conversations on scroll
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(linearLayoutManager) {
				override fun onLoadMore(page: Int, totalItemsCount: Int) {

					if (linearLayoutManager.findLastVisibleItemPosition() == totalItemsCount - 4){
						//Log.wtf(TAG, "load seems to be called")
						conversationsViewModel.loadConversationsList()
					}

				}
			})

			val swipeHandler = object : SwipeToDeleteCallback(context) {
				override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

					val adapter = rvConversationList.adapter as ConversationsAdapter
					val itemPosition = viewHolder.adapterPosition

					MaterialAlertDialogBuilder(context)
						.setTitle("Удалить диалог?")
						.setMessage("Это полностью удалит переписку и пару с пользователем")
						.setPositiveButton("Удалить") { dialog, _ ->
							conversationsViewModel.deleteConversation(adapter.getItem(itemPosition))
							adapter.removeAt(itemPosition)
							conversationsViewModel.getDeleteConversationStatus()
								.observeOnce(this@ConversationsFragment, Observer {
									if (it) snackbar.show()
								})
							dialog.dismiss()
						}
						.setNegativeButton("Отмена") { dialog, _ ->
							adapter.notifyItemChanged(itemPosition)
							dialog.dismiss()
						}
						.show()
				}
			}
			val itemTouchHelper = ItemTouchHelper(swipeHandler)
			itemTouchHelper.attachToRecyclerView(this)

		}

		mConversationsAdapter.setOnItemClickListener(object: BaseAdapter.OnItemClickListener<ConversationItem> {

			override fun onItemClick(item: ConversationItem, position: Int) {
				sharedViewModel.setConversationSelected(item)
				findNavController().navigate(R.id.action_conversations_to_chatFragment)
			}
		})

	}

	private fun makeSnackbar(view: View) = Snackbar.make(view, "Successfully deleted", Snackbar.LENGTH_SHORT)

}