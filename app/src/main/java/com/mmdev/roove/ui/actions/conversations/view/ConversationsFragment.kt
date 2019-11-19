/*
 * Created by Andrii Kovalchuk on 27.10.19 21:06
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.11.19 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.actions.conversations.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.actions.conversations.viewmodel.ConversationsViewModel
import com.mmdev.roove.ui.main.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: Fragment(R.layout.fragment_conversations){

	private lateinit var mMainActivity: MainActivity

	private val mConversationsAdapter: ConversationsAdapter = ConversationsAdapter(listOf())

	private lateinit var conversationsVM: ConversationsViewModel
	private val conversationsVMFactory = injector.conversationsViewModelFactory()

	private val disposables = CompositeDisposable()



	companion object {

		private const val TAG = "mylogs"

		fun newInstance():  ConversationsFragment {
			return  ConversationsFragment()
		}

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }

		conversationsVM = ViewModelProvider(mMainActivity, conversationsVMFactory).get(ConversationsViewModel::class.java)

		//get active conversations list
		disposables.add(conversationsVM.getConversationsList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           Log.wtf(TAG, "conversations to show: ${it.size}, conv_frag")
                           mConversationsAdapter.updateData(it)
                       },
                       {
                           Log.wtf(TAG, "error + $it")
                       }))

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val rvConversationsList = view.findViewById<RecyclerView>(R.id.active_conversations_rv)
		//mConversationsAdapter.updateData(generateConversationsList())
		rvConversationsList.apply {
			adapter = mConversationsAdapter
			layoutManager = LinearLayoutManager(context, VERTICAL, false)
			itemAnimator = DefaultItemAnimator()
		}

		mConversationsAdapter.setOnItemClickListener(object: ConversationsAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {
				val conversationItem = mConversationsAdapter.getConversationItem(position)

				mMainActivity.conversationItemClicked = conversationItem
				mMainActivity.partnerName = conversationItem.partnerName

				// if conversation is stored in conversations container
				// seems conversation was started and valid id is given
				mMainActivity.startChatFragment(conversationItem.conversationId)

			}
		})


	}


	private fun generateConversationsList(): List<ConversationItem>{
		val conversationItemList = ArrayList<ConversationItem>()
		conversationItemList.add(ConversationItem("",
		                                          "Tvoi paren",
		                                          "",
		                                          "ты че сука"))
		conversationItemList.add(ConversationItem("",
		                                          "Майор миллиции",
		                                          "",
		                                          "priidi v uchastok"))
		conversationItemList.add(ConversationItem("",
		                                          "Мой парень",
		                                          "",
		                                          "привет любимая"))
		conversationItemList.add(ConversationItem("",
		                                          "Рандомный чел",
		                                          "",
		                                          "скинь сиськи"))
		conversationItemList.add(ConversationItem("",
		                                          "Турок",
		                                          "",
		                                          "дай писка лизат"))
		conversationItemList.add(ConversationItem("",
		                                          "Твой батя",
		                                          "",
		                                          "не еби мою дочь"))

		return conversationItemList
	}


	override fun onDestroy() {
		super.onDestroy()
		disposables.clear()
	}
}