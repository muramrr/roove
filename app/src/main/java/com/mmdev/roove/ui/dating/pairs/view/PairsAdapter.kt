/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.02.20 17:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.pairs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.cards.MatchedUserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPairsItemBinding


/* Created by A on 13.11.2019.*/

/**
 * This is the documentation block about the class
 */

class PairsAdapter (private var mPairsList: List<MatchedUserItem>):
		RecyclerView.Adapter<PairsAdapter.PairsViewHolder>() {


	private lateinit var clickListener: OnItemClickListener


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PairsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                                R.layout.fragment_pairs_item,
		                                                parent,
		                                                false))

	override fun onBindViewHolder(holder: PairsViewHolder, position: Int) =
		holder.bind(mPairsList[position])


	override fun getItemCount() = mPairsList.size

	fun updateData(newPairsList: List<MatchedUserItem>) {
		mPairsList = newPairsList
		notifyDataSetChanged()
	}

	fun getPairItem(position: Int) = mPairsList[position]

	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		clickListener = itemClickListener
	}


	inner class PairsViewHolder (private val binding: FragmentPairsItemBinding):
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
		fun bind(matchedItem: MatchedUserItem){
			binding.matchedItem = matchedItem
			binding.executePendingBindings()
		}
	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}
}