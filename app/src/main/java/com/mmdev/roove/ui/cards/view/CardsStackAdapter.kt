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
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.business.user.UserItem
import com.mmdev.roove.BR
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentCardsItemBinding
import com.mmdev.roove.ui.cards.view.CardsStackAdapter.CardsViewHolder
import com.mmdev.roove.ui.common.ImagePagerAdapter

class CardsStackAdapter (private var usersList: List<UserItem> = emptyList()):
		RecyclerView.Adapter<CardsViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		CardsViewHolder(
			DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				R.layout.fragment_cards_item,
				parent,
				false
			)
		)


	override fun onBindViewHolder(holderCards: CardsViewHolder, position: Int) =
		holderCards.bind(usersList[position])


	override fun getItemCount() = usersList.size

	fun getItem(position: Int) = usersList[position]

	fun setData(newCardItems: List<UserItem>) {
		usersList = newCardItems
		notifyDataSetChanged()
	}
	
	private var clickListener: ((UserItem, Int) -> Unit)? = null
	// allows clicks events to be caught
	fun setOnItemClickListener(listener: (UserItem, Int) -> Unit) {
		clickListener = listener
	}

	inner class CardsViewHolder(private val binding: FragmentCardsItemBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		init {
			itemView.setOnClickListener {
				clickListener?.invoke(usersList[adapterPosition], adapterPosition)
			}
		}
		
		fun bind(userItem: UserItem) = binding.run {
			
			viewPagerCardPhotoList.run {
				adapter = ImagePagerAdapter()
				isUserInputEnabled = false
			}
			TabLayoutMediator(tlCardPhotosIndicator, viewPagerCardPhotoList) { _: TabLayout.Tab, _: Int ->
				//do nothing
			}.attach()

			nextImage.setOnClickListener {
				if (viewPagerCardPhotoList.currentItem != userItem.photoURLs.size - 1) viewPagerCardPhotoList.currentItem++
			}
			previousImage.setOnClickListener {
				if (viewPagerCardPhotoList.currentItem != 0) viewPagerCardPhotoList.currentItem--
			}
			
			setVariable(BR.bindItem, userItem)
			executePendingBindings()
		}

	}
	
}