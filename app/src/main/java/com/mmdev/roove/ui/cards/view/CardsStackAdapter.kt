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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentCardsItemBinding
import com.mmdev.roove.ui.cards.view.CardsStackAdapter.CardsViewHolder
import com.mmdev.roove.ui.common.ImagePagerAdapter

class CardsStackAdapter (private var usersList: List<UserItem> = emptyList()):
		RecyclerView.Adapter<CardsViewHolder>() {


	private lateinit var clickListener: OnItemClickListener


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		CardsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                        R.layout.fragment_cards_item,
		                                        parent,
		                                        false))


	override fun onBindViewHolder(holderCards: CardsViewHolder, position: Int) =
		holderCards.bind(usersList[position])


	override fun getItemCount() = usersList.size

	fun getItem(position: Int) = usersList[position]

	fun setData(newCardItems: List<UserItem>) {
		usersList = newCardItems
		notifyDataSetChanged()
	}

	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		clickListener = itemClickListener
	}

	inner class CardsViewHolder (private val binding: FragmentCardsItemBinding):
			RecyclerView.ViewHolder(binding.root) {

		private val vp = itemView.findViewById<ViewPager2>(R.id.viewPagerCardPhotoList)
		private val next = itemView.findViewById<View>(R.id.nextImage)
		private val previous = itemView.findViewById<View>(R.id.previousImage)
		private val tabIndicator = itemView.findViewById<TabLayout>(R.id.tlCardPhotosIndicator)

		init {
			itemView.setOnClickListener {
				clickListener.onItemClick(usersList[adapterPosition], adapterPosition)
			}
		}

		/*
		*   executePendingBindings()
		*   Evaluates the pending bindings,
		*   updating any Views that have expressions bound to modified variables.
		*   This must be run on the UI thread.
		*/
		fun bind(userItem: UserItem){
			binding.bindItem = userItem

			vp.apply {
				adapter = ImagePagerAdapter()
				isUserInputEnabled = false
			}
			TabLayoutMediator(tabIndicator, vp) { _: TabLayout.Tab, _: Int ->
				//do nothing
			}.attach()

			next.setOnClickListener { if (vp.currentItem != userItem.photoURLs.size - 1) vp.currentItem++ }
			previous.setOnClickListener { if (vp.currentItem != 0) vp.currentItem-- }

			binding.executePendingBindings()
		}

	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(item: UserItem, position: Int)
	}

}