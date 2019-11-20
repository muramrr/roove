/*
 * Created by Andrii Kovalchuk on 20.11.19 21:38
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.11.19 21:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.events.view.tabitem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.events.model.EventItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentEventsRvItemBinding

class EventsRecyclerAdapter (private var mEventsList: List<EventItem>):
		RecyclerView.Adapter<EventsRecyclerAdapter.FeedItemHolder>() {

	private lateinit var mClickListener: OnItemClickListener

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		FeedItemHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                       R.layout.fragment_events_rv_item,
		                                       parent,
		                                       false))


	override fun onBindViewHolder(holder: FeedItemHolder, position: Int) =
		holder.bind(mEventsList[position])


	override fun getItemCount() = mEventsList.size

	fun updateData(newEvents: List<EventItem>) {
		mEventsList = newEvents
		notifyDataSetChanged()
	}

	fun getEventItem(position: Int) = mEventsList[position]


	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		mClickListener = itemClickListener
	}


	inner class FeedItemHolder(private val binding: FragmentEventsRvItemBinding) :
			RecyclerView.ViewHolder(binding.root) {

		init {
			itemView.setOnClickListener {
				mClickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		fun bind(eventItem: EventItem) {
			binding.eventItem = eventItem
			binding.executePendingBindings()
		}

	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}

}
