/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

package com.mmdev.roove.ui.conversations.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.domain.conversations.ConversationItem
import com.mmdev.roove.BR
import com.mmdev.roove.core.log.logWtf
import com.mmdev.roove.databinding.ItemConversationBinding
import java.util.*

class ConversationsAdapter(
	private var data: MutableList<ConversationItem> = mutableListOf()
): RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {
	
	private companion object{
		private const val FIRST_POS = 0
		private const val ITEMS_PER_PAGE = 20
		private const val OPTIMAL_ITEMS_COUNT = ITEMS_PER_PAGE * 2
	}
	private var startPos = 0
	private var itemsLoaded = 0
	
	
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
		ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
	)
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])
	
	override fun getItemCount() = data.size
	
	fun getItem(position: Int) = data[position]
	
	private var mClickListener: ((ConversationItem, Int) -> Unit)? = null
	// allows clicks events to be caught
	fun setOnItemClickListener(listener: (ConversationItem, Int) -> Unit) { mClickListener = listener }
	
	
	private var loadPrevListener: ((Date) -> Unit)? = null
	private var loadNextListener: ((Date) -> Unit)? = null
	
	fun setLoadPrevListener(listener: (Date) -> Unit) { loadPrevListener = listener }
	fun setLoadNextListener(listener: (Date) -> Unit) { loadNextListener = listener }

	fun setNewData(newData: List<ConversationItem>) {
		data.clear()
		data.addAll(newData)
		notifyDataSetChanged()
	}
	
	fun insertPreviousData(topData: List<ConversationItem>) {
		data.addAll(FIRST_POS, topData)
		notifyItemRangeInserted(FIRST_POS, topData.size)
		
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			val shouldBeRemovedCount = data.size - OPTIMAL_ITEMS_COUNT
			data = data.dropLast(shouldBeRemovedCount).toMutableList()
			itemsLoaded -= shouldBeRemovedCount
			notifyItemRangeRemoved((data.size - 1), shouldBeRemovedCount)
		}
	}
	
	
	fun insertNextData(bottomData: List<ConversationItem>) {
		startPos = data.size
		data.addAll(bottomData)
		itemsLoaded += bottomData.size
		notifyItemRangeInserted(startPos, bottomData.size)
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			val shouldBeRemovedCount = data.size - OPTIMAL_ITEMS_COUNT
			data = data.drop(
				if (shouldBeRemovedCount < ITEMS_PER_PAGE) shouldBeRemovedCount - 1
				else shouldBeRemovedCount
			).toMutableList()
			notifyItemRangeRemoved(FIRST_POS, shouldBeRemovedCount)
		}
	}

	fun removeAt(position: Int) {
		data.removeAt(position)
		notifyItemRemoved(position)
	}
	
	inner class ViewHolder(private val binding: ViewDataBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		init {
			mClickListener?.let { mClickListener ->
				itemView.setOnClickListener {
					mClickListener.invoke(data[adapterPosition], adapterPosition)
				}
			}
		}
		
		fun bind(item: ConversationItem) {
			if (adapterPosition > 9 && adapterPosition == (data.size - 10))
				loadNextListener?.invoke(data.last().lastMessageTimestamp!!)
			
			if (itemsLoaded >= data.size && adapterPosition == 10) {
				loadPrevListener?.invoke(data.first().lastMessageTimestamp!!)
				logWtf("mylogs_adapter", "invoked load prev with $itemsLoaded, ${data.size}")
			}
			
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
}