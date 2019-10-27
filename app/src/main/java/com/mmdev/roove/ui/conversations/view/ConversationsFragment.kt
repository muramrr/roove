package com.mmdev.roove.ui.conversations.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R
import com.mmdev.roove.ui.main.view.MainActivity

/* Created by A on 27.10.2019.*/

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: Fragment(R.layout.fragment_conversations){

	private lateinit var  mMainActivity: MainActivity

	private lateinit var rvConversationsList: RecyclerView
	private var mConversationsAdapter: ConversationsAdapter = ConversationsAdapter(listOf())

	companion object{
		fun newInstance():  ConversationsFragment {
			return  ConversationsFragment()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		activity?.let { mMainActivity = it as MainActivity }

		rvConversationsList = view.findViewById(R.id.conversations_rv)

		initConversations()

		mConversationsAdapter.setOnItemClickListener(object: ConversationsAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {
				Toast.makeText(context,
				               mConversationsAdapter.getFeedItem(position).lastMessageText,
				               Toast.LENGTH_SHORT).show()
			}
		})
	}

	private fun initConversations() {
		mConversationsAdapter.updateData(ConversationsManager.generateConversationsList())

		rvConversationsList.apply {
			adapter = mConversationsAdapter
			layoutManager = LinearLayoutManager(context, VERTICAL, false)
			itemAnimator = DefaultItemAnimator()
		}
	}


	override fun onResume() {
		super.onResume()
		mMainActivity.toolbar.title = "Messages"
	}
}