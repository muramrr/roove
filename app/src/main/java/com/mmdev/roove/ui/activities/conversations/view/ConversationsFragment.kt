package com.mmdev.roove.ui.activities.conversations.view

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
import com.mmdev.roove.ui.activities.conversations.viewmodel.ConversationsViewModel
import com.mmdev.roove.ui.main.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/* Created by A on 27.10.2019.*/

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
                           Log.wtf(TAG, "users to show: ${it.size}")
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

				mMainActivity.startChatFragment(mConversationsAdapter
					                                .getConversationItem(position)
					                                .conversationId)

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