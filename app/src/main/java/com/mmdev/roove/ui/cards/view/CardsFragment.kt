/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 07.12.19 18:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.cards.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mmdev.business.cards.model.CardItem
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.databinding.FragmentCardsBinding
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.cards.CardsViewModel
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.utils.addSystemBottomPadding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import kotlinx.android.synthetic.main.fragment_cards.*


class CardsFragment: BaseFragment() {

	private val mCardsStackAdapter = CardsStackAdapter(listOf())

	private lateinit var mAppearedCardItem: CardItem
	private lateinit var mDisappearedCardItem: CardItem

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var cardsViewModel: CardsViewModel
	private val factory = injector.factory()


	companion object{

		fun newInstance() = CardsFragment()

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		cardsViewModel = ViewModelProvider(this, factory)[CardsViewModel::class.java]
		cardsViewModel.loadUsersByPreferences()
		cardsViewModel.getUsersCardsList().observe(this, Observer {
			mCardsStackAdapter.updateData(it)
		})
		cardsViewModel.showMatchDialog.observe(this, Observer {
			if (it) showMatchDialog(mDisappearedCardItem)
		})

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentCardsBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@CardsFragment
				viewModel = cardsViewModel
				executePendingBindings()
			}
			.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		cardStackView.addSystemBottomPadding()

		val cardStackLayoutManager = CardStackLayoutManager(cardStackView.context,
		                                                    object: CardStackListener {

			override fun onCardAppeared(view: View, position: Int) {
				//get current displayed on card profile
				mAppearedCardItem = mCardsStackAdapter.getCardItem(position)

			}

			override fun onCardDragging(direction: Direction, ratio: Float) {}

			override fun onCardSwiped(direction: Direction) {
				//if right = add to liked
				//else = add to skipped
				if (direction == Direction.Right) {
					cardsViewModel.checkMatch(mAppearedCardItem)
				}

				if (direction == Direction.Left) {
					cardsViewModel.addToSkipped(mAppearedCardItem)
				}
			}

			override fun onCardRewound() {}
			override fun onCardCanceled() {}

			override fun onCardDisappeared(view: View, position: Int) {
				//if there is no available user to show - show loading
				mDisappearedCardItem = mCardsStackAdapter.getCardItem(position)
				if (position == mCardsStackAdapter.itemCount - 1) {
					cardsViewModel.showLoading.value = true
					cardsViewModel.showTextHelper.value = true
				}
			}

		})

		cardStackView.apply {
			adapter = mCardsStackAdapter
			layoutManager = cardStackLayoutManager
		}

		mCardsStackAdapter.setOnItemClickListener(object: CardsStackAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {

				sharedViewModel.setCardSelected(mCardsStackAdapter.getCardItem(position))

				findNavController().navigate(R.id.action_nav_cards_to_profileFragment)
			}
		})

	}


	private fun showMatchDialog(matchCardItem: CardItem) {

		val dialog = MatchDialogFragment.newInstance(
				matchCardItem.name, matchCardItem.mainPhotoUrl
		)

		dialog.show(childFragmentManager, MatchDialogFragment::class.java.canonicalName)
	}


}
