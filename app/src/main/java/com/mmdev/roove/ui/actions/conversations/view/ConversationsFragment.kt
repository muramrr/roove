/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 05.12.19 19:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions.conversations.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.actions.conversations.ConversationsViewModel
import com.mmdev.roove.ui.chat.view.ChatFragment
import com.mmdev.roove.utils.replaceFragmentInDrawer
import kotlinx.android.synthetic.main.fragment_conversations.*

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: Fragment(R.layout.fragment_conversations){

	private val mConversationsAdapter = ConversationsAdapter(listOf())

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var conversationsViewModel: ConversationsViewModel
	private val factory = injector.factory()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")


		conversationsViewModel = ViewModelProvider(this, factory)[ConversationsViewModel::class.java]

		conversationsViewModel.loadConversationsList()

		conversationsViewModel.getConversationsList().observe(this, Observer {
			mConversationsAdapter.updateData(it)
		})

	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		rvConversationList.apply {
			adapter = mConversationsAdapter
			layoutManager = LinearLayoutManager(context, VERTICAL, false)
			itemAnimator = DefaultItemAnimator()
		}

		mConversationsAdapter.setOnItemClickListener(object: ConversationsAdapter.OnItemClickListener {

			override fun onItemClick(view: View, position: Int) {
				sharedViewModel.setConversationSelected(mConversationsAdapter.getConversationItem(position))

				childFragmentManager.replaceFragmentInDrawer(ChatFragment.newInstance())
			}

		})


	}

}