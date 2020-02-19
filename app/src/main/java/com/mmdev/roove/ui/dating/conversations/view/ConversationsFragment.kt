/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.02.20 13:55
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentConversationsBinding
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.dating.conversations.ConversationsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_conversations.*

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: BaseFragment(R.layout.fragment_conversations){

	private val mConversationsAdapter =
		ConversationsAdapter(listOf())

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var conversationsViewModel: ConversationsViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")


		conversationsViewModel = ViewModelProvider(this@ConversationsFragment, factory)[ConversationsViewModel::class.java]

		conversationsViewModel.getConversationsList().observe(this, Observer {
			mConversationsAdapter.updateData(it)
		})

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentConversationsBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@ConversationsFragment
				viewModel = conversationsViewModel
				executePendingBindings()
			}
			.root

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
		}

		mConversationsAdapter.setOnItemClickListener(object: ConversationsAdapter.OnItemClickListener {

			override fun onItemClick(view: View, position: Int) {

				sharedViewModel.setConversationSelected(mConversationsAdapter.getConversationItem(position))

				findNavController().navigate(R.id.action_conversations_to_chatFragment)

			}

		})

	}

	override fun onResume() {
		super.onResume()
		conversationsViewModel.loadConversationsList()
	}

}