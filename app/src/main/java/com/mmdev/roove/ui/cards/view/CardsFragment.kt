/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.cards.view


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentCardsBinding
import com.mmdev.roove.ui.cards.CardsViewModel
import com.mmdev.roove.ui.common.base.BaseFragment
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CardsFragment: BaseFragment<CardsViewModel, FragmentCardsBinding>(
	layoutId = R.layout.fragment_cards
) {
	
	override val mViewModel: CardsViewModel by viewModels()
	
	private val mCardsStackAdapter = CardsStackAdapter()

	private var mCardsList = mutableListOf<UserItem>()

	private var mAppearedUserItem: UserItem = UserItem()
	private var mDisappearedUserItem: UserItem = UserItem()



	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mViewModel.loadUsersByPreferences(initialLoading = true)
		mViewModel.usersCardsList.observe(this, {
			mCardsList.clear()
			mCardsList.addAll(it)
			mCardsStackAdapter.setData(it)
		})
		mViewModel.showMatchDialog.observe(this, {
			if (it) showMatchDialog(mDisappearedUserItem)
		})

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {

		val cardStackLayoutManager = CardStackLayoutManager(
			cardStackView.context, object: CardStackListener {

			override fun onCardAppeared(view: View, position: Int) {
				//get current displayed on card profile
				mAppearedUserItem = mCardsStackAdapter.getItem(position)
			}

			override fun onCardDragging(direction: Direction, ratio: Float) {}

			override fun onCardSwiped(direction: Direction) {
				//if right = add to liked
				if (direction == Direction.Right) {
					mViewModel.checkMatch(mAppearedUserItem)
				}
				//left = add to skipped
				if (direction == Direction.Left) {
					mViewModel.addToSkipped(mAppearedUserItem)
				}
			}

			override fun onCardRewound() {}
			override fun onCardCanceled() {}

			override fun onCardDisappeared(view: View, position: Int) {
				//needed to show match
				mDisappearedUserItem = mCardsStackAdapter.getItem(position)
				mCardsList.remove(mDisappearedUserItem)
				if (position == mCardsStackAdapter.itemCount - 1) {
					mViewModel.loadUsersByPreferences()
				}

			}

		})

		cardStackView.apply {
			adapter = mCardsStackAdapter
			layoutManager = cardStackLayoutManager
			itemAnimator.apply {
				if (this is DefaultItemAnimator) {
					supportsChangeAnimations = false
				}
			}
		}

		mCardsStackAdapter.setOnItemClickListener { item, position ->
			sharedViewModel.userNavigateTo.value = item
			navController.navigate(R.id.action_cards_to_profileFragment)
		}

	}

	override fun onStop() {
		super.onStop()
		if (mCardsStackAdapter.itemCount != mCardsList.size)
			mCardsStackAdapter.setData(mCardsList.toList())
	}

	private fun showMatchDialog(userItem: UserItem) = MatchDialogFragment.newInstance(
		userItem.baseUserInfo.name, userItem.baseUserInfo.mainPhotoUrl
	).show(childFragmentManager, MatchDialogFragment::class.java.canonicalName)
	

}
