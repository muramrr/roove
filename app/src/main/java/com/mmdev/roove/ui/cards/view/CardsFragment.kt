/*
 * Created by Andrii Kovalchuk on 26.11.19 20:29
 * Copyright (c) 2019. All rights reserved.
 * Last modified 26.11.19 20:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.cards.view


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mmdev.business.cards.model.CardItem
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.core.injector
import com.mmdev.roove.databinding.FragmentCardsBinding
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModel
import com.mmdev.roove.ui.main.view.MainActivity
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction


class CardsFragment: Fragment() {

	private lateinit var mMainActivity: MainActivity

	private val mCardsStackAdapter: CardsStackAdapter = CardsStackAdapter(listOf())

	private lateinit var mAppearedCardItem: CardItem

	private lateinit var cardsViewModel: CardsViewModel


	companion object{

		fun newInstance() = CardsFragment()

	}

	//TODO: FIX BUG WITH MATCH DIALOG

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }

		val factory = injector.factory()
		cardsViewModel = ViewModelProvider(this, factory)[CardsViewModel::class.java]
		cardsViewModel.loadUsersByPreferences()
		cardsViewModel.getUsersCardsList().observe(this, Observer {
			mCardsStackAdapter.updateData(it)
		})
		cardsViewModel.showMatchDialog.observe(this, Observer {
			if (it) showMatchDialog(mAppearedCardItem)
		})

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentCardsBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@CardsFragment
				viewModel = cardsViewModel
			}
			.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val cardStackView = view.findViewById<CardStackView>(R.id.card_stack_view)
		val loadingImageView = view.findViewById<ImageView>(R.id.card_loading_progress_iv)
		initLoadingGif(loadingImageView)

		val cardStackLayoutManager = CardStackLayoutManager(mMainActivity, object: CardStackListener {

			override fun onCardAppeared(view: View, position: Int) {
				//get current displayed on card profile
				mAppearedCardItem = mCardsStackAdapter.getCardItem(position)

			}

			override fun onCardDragging(direction: Direction, ratio: Float) {}

			override fun onCardSwiped(direction: Direction) {
				//if right = add to liked
				//else = add to skipped
				if (direction == Direction.Right) {
					cardsViewModel.handlePossibleMatch(mAppearedCardItem)
				}

				if (direction == Direction.Left) {
					cardsViewModel.addToSkipped(mAppearedCardItem)
				}
			}

			override fun onCardRewound() {}

			override fun onCardCanceled() {}

			override fun onCardDisappeared(view: View, position: Int) {
				//if there is no available user to show - show loading
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

		mCardsStackAdapter.setOnItemClickListener(object: CardsStackAdapter.OnItemClickListener{
			override fun onItemClick(view: View, position: Int) {
				mMainActivity.startProfileFragment(mCardsStackAdapter.getCardItem(position).userId,
				                                   false)
			}
		})

	}


	private fun showMatchDialog(matchCardItem: CardItem) {
		val matchDialog = Dialog(mMainActivity)
		matchDialog.setContentView(R.layout.dialog_match)
		//matchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//matchDialog.getWindow().setDimAmount(0.87f);
		matchDialog.show()
		matchDialog.window!!.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
		val backgr = matchDialog.findViewById<ImageView>(R.id.diag_match_iv_backgr_profile_img)
		GlideApp.with(backgr.context)
			.load(matchCardItem.mainPhotoUrl)
			.centerInside()
			.into(backgr)
		matchDialog.findViewById<View>(R.id.diag_match_tv_keep_swp).setOnClickListener { matchDialog.dismiss() }
	}


	private fun initLoadingGif(loadingImageView: ImageView){
		Glide.with(loadingImageView.context)
			.asGif()
			.load(R.drawable.loading)
			.centerCrop()
			.apply(RequestOptions().circleCrop())
			.into(loadingImageView)
	}

	override fun onResume() {
		super.onResume()
		mMainActivity.toolbar.title = "Cards"
		mMainActivity.setNonScrollableToolbar()
	}


}
