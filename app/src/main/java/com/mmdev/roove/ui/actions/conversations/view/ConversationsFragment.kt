/*
 * Created by Andrii Kovalchuk on 27.11.19 19:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 27.11.19 19:15
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
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.actions.conversations.ConversationsViewModel
import com.mmdev.roove.ui.main.view.MainActivity

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: Fragment(R.layout.fragment_conversations){


	private lateinit var mMainActivity: MainActivity

	private val mConversAdapter =
		ConversationsAdapter(listOf())


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }

		val factory = injector.factory()

		val conversViewModel =
			ViewModelProvider(this, factory)[ConversationsViewModel::class.java]

		conversViewModel.loadConversationsList()

		conversViewModel.getConversationsList().observe(this, Observer {
			mConversAdapter.updateData(it)
		})

	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val rvConversationsList = view.findViewById<RecyclerView>(R.id.active_conversations_rv)
		rvConversationsList.apply {
			adapter = mConversAdapter
			layoutManager = LinearLayoutManager(context, VERTICAL, false)
			itemAnimator = DefaultItemAnimator()
		}

		mConversAdapter.setOnItemClickListener(object: ConversationsAdapter.OnItemClickListener {

			override fun onItemClick(view: View, position: Int) {
				val conversationItem = mConversAdapter.getConversationItem(position)

				mMainActivity.conversationItemClicked = conversationItem

				mMainActivity.partnerId = conversationItem.partnerId
				mMainActivity.partnerMainPhotoUrl = conversationItem.partnerPhotoUrl
				mMainActivity.partnerName = conversationItem.partnerName

				// if conversation is stored in conversations container
				// seems conversation was started and valid id is given
				mMainActivity.startChatFragment(conversationItem.conversationId)

			}

		})


	}

}