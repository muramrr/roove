/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 19.12.19 21:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.cards.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.cards.CardItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentCardsItemBinding

class CardsStackAdapter (private var cardsList: List<CardItem>):
		RecyclerView.Adapter<CardsStackAdapter.CardsViewHolder>() {


	private lateinit var clickListener: OnItemClickListener


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		CardsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                        R.layout.fragment_cards_item,
		                                        parent,
		                                        false))


	override fun onBindViewHolder(holderCards: CardsViewHolder, position: Int) {
		holderCards.bind(cardsList[position])
	}

	override fun getItemCount() = cardsList.size

	fun getCardItem(position: Int) = cardsList[position]

	fun updateData(newCardItems: List<CardItem>) {
		cardsList = newCardItems
		notifyDataSetChanged()
	}

	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		clickListener = itemClickListener
	}

	inner class CardsViewHolder (private val binding: FragmentCardsItemBinding):
			RecyclerView.ViewHolder(binding.root) {

		init {
			itemView.setOnClickListener {
				clickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		/*
		*   executePendingBindings()
		*   Evaluates the pending bindings,
		*   updating any Views that have expressions bound to modified variables.
		*   This must be run on the UI thread.
		*/
		fun bind(cardItem: CardItem){
			binding.cardItem = cardItem
			binding.executePendingBindings()
		}

	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}

}
