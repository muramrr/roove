/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.01.20 21:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.base.BasePlaceInfo
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentProfilePlacesRvItemBinding

/**
 * This is the documentation block about the class
 */

class ProfilePlacesToGoAdapter (private var mPlacesToGoList: List<BasePlaceInfo>):
		RecyclerView.Adapter<ProfilePlacesToGoAdapter.PlacesToGoItemHolder>() {

	private lateinit var mClickListener: OnItemClickListener

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PlacesToGoItemHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                             R.layout.fragment_profile_places_rv_item,
		                                             parent,
		                                             false))


	override fun onBindViewHolder(holder: PlacesToGoItemHolder, position: Int) =
		holder.bind(mPlacesToGoList[position])


	override fun getItemCount() = mPlacesToGoList.size

	fun updateData(newPlacesToGoList: List<BasePlaceInfo>) {
		mPlacesToGoList = newPlacesToGoList
		notifyDataSetChanged()
	}

	fun getPlaceToGoItem(position: Int) = mPlacesToGoList[position]


	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		mClickListener = itemClickListener
	}

	inner class PlacesToGoItemHolder(private val binding: FragmentProfilePlacesRvItemBinding) :
			RecyclerView.ViewHolder(binding.root) {

		init {
			itemView.setOnClickListener {
				mClickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		/*
		*   executePendingBindings()
		*   Evaluates the pending bindings,
		*   updating any Views that have expressions bound to modified variables.
		*   This must be run on the UI thread.
		*/
		fun bind(basePlaceInfo: BasePlaceInfo) {
			binding.basePlaceInfo = basePlaceInfo
			binding.executePendingBindings()
		}

	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}

}