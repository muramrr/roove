package com.mmdev.roove.ui.conversations.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModel
import com.mmdev.roove.ui.conversations.viewmodel.ConversationsViewModel
import com.mmdev.roove.ui.main.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/* Created by A on 27.10.2019.*/

/**
 * This is the documentation block about the class
 */

class ConversationsFragment: Fragment(R.layout.fragment_conversations){

	private lateinit var  mMainActivity: MainActivity

	private lateinit var rvActiveConversationsList: RecyclerView
	private val mActiveConversationsAdapter: ActiveConversationsAdapter = ActiveConversationsAdapter(listOf())

	private lateinit var rvPotentialPartnersList: RecyclerView
	private val mPotentialPartnersAdapter: PotentialPartnersAdapter = PotentialPartnersAdapter(listOf())

	//for potential
	private lateinit var cardsViewModel: CardsViewModel
	private val cardsViewModelFactory = injector.cardsViewModelFactory()

	//for active
	private lateinit var convViewModel: ConversationsViewModel
	private val convViewModelFactory = injector.conversationsViewModelFactory()

	private val disposables = CompositeDisposable()

	companion object{
		fun newInstance():  ConversationsFragment {
			return  ConversationsFragment()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }

		cardsViewModel = ViewModelProvider(mMainActivity, cardsViewModelFactory).get(CardsViewModel::class.java)
		convViewModel = ViewModelProvider(mMainActivity, convViewModelFactory).get(ConversationsViewModel::class.java)
		//get matched users
		disposables.add(cardsViewModel.getMatchedUserItems()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           Log.wtf("mylogs", "potential users to show: ${it.size}")
	                       mPotentialPartnersAdapter.updateData(it)
                       },
                       {
                           Log.wtf("mylogs", "error + $it")
                       }))
		//get active conversations list
		disposables.add(convViewModel.getConversationsList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           Log.wtf("mylogs", "users to show: ${it.size}")
                           mActiveConversationsAdapter.updateData(it)
                       },
                       {
                           Log.wtf("mylogs", "error + $it")
                       }))

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		rvActiveConversationsList = view.findViewById(R.id.active_conversations_rv)
		rvPotentialPartnersList = view.findViewById(R.id.potential_partners_rv)

		initActiveConversations()
		initPotentialPartners()


	}

	private fun initActiveConversations() {
		//mActiveConversationsAdapter.updateData(ConversationsManager.generateConversationsList())
		rvActiveConversationsList.apply {
			adapter = mActiveConversationsAdapter
			layoutManager = LinearLayoutManager(context, VERTICAL, false)
			itemAnimator = DefaultItemAnimator()
		}

		mActiveConversationsAdapter.setOnItemClickListener(object: ActiveConversationsAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {

				mMainActivity.startChatFragment(mActiveConversationsAdapter
					                                .getConversationItem(position).conversationId)

				}
		})

	}

	private fun initPotentialPartners() {
		rvPotentialPartnersList.apply {
			adapter = mPotentialPartnersAdapter
			layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
			itemAnimator = DefaultItemAnimator()
		}

		mPotentialPartnersAdapter.setOnItemClickListener(object: PotentialPartnersAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {
				disposables.add(convViewModel.createConversation(mPotentialPartnersAdapter.getPotentialPartnerItem(position))
	                .observeOn(AndroidSchedulers.mainThread())
	                .subscribe({
		                           Log.wtf("mylogs", "conversation created = ${it.conversationId}")
		                           mMainActivity.startChatFragment(it.conversationId)
	                           },
	                           {
		                           Log.wtf("mylogs", "error + $it")
	                           }))
			}
		})
	}


	override fun onResume() {
		super.onResume()
		mMainActivity.toolbar.title = "Messages"
	}

	override fun onDestroy() {
		super.onDestroy()
		disposables.clear()
	}
}