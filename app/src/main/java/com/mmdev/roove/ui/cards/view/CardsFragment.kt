/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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
