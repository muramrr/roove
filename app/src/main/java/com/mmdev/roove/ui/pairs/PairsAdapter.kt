/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2022. roove
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

package com.mmdev.roove.ui.pairs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.roove.BR
import com.mmdev.roove.databinding.ItemPairBinding
import com.mmdev.roove.ui.pairs.PairsAdapter.ViewHolder
import java.util.*


class PairsAdapter(
	private var data: MutableList<MatchedUserItem> = mutableListOf()
): RecyclerView.Adapter<ViewHolder>() {
	
	private companion object{
		private const val FIRST_POS = 0
		private const val ITEMS_PER_PAGE = 10
		private const val OPTIMAL_ITEMS_COUNT = ITEMS_PER_PAGE * 2
	}
	private var startPos = 0
	private var itemsLoaded = 0
	private var pageTop = 0
		set(value) {
			field = if (value < 0) 0
			else value
		}
	private var pageBottom = 0
		set(value) {
			if (value > 1) pageTop = value - 1
			field = value
		}
		get() = if (field < 0) 0 else field
	private var isLastPage = false
	
	
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ItemPairBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		binding.root.post { binding.root.layoutParams.height = parent.height / 2 - 100 }
		return ViewHolder(binding)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])
	
	override fun getItemCount() = data.size
	
	
	private var mClickListener: ((MatchedUserItem, Int) -> Unit)? = null
	
	// allows clicks events to be caught
	fun setOnItemClickListener(listener: (MatchedUserItem, Int) -> Unit) {
		mClickListener = listener
	}
	
	private var loadPrevListener: ((Int) -> Unit)? = null
	private var loadNextListener: ((Date, Int) -> Unit)? = null
	
	fun setLoadPrevListener(listener: (Int) -> Unit) { loadPrevListener = listener }
	fun setLoadNextListener(listener: (Date, Int) -> Unit) { loadNextListener = listener }
	
	
	fun setNewData(newData: List<MatchedUserItem>) {
		data.clear()
		data.addAll(newData)
		itemsLoaded = newData.size
		//data = newData as MutableList<MatchedUserItem>
		notifyDataSetChanged()
	}
	
	fun insertPreviousData(topData: List<MatchedUserItem>) {
		//update offsets
		isLastPage = false
		if (topData.size == ITEMS_PER_PAGE) pageBottom = pageTop
		
		data.addAll(FIRST_POS, topData)
		notifyItemRangeInserted(FIRST_POS, topData.size)
		
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			val shouldBeRemovedCount = ITEMS_PER_PAGE
			data = data.dropLast(shouldBeRemovedCount).toMutableList()
			itemsLoaded -= shouldBeRemovedCount
			notifyItemRangeRemoved((data.size - 1), shouldBeRemovedCount)
		}
	}
	
	
	fun insertNextData(bottomData: List<MatchedUserItem>) {
		//update offsets
		pageBottom++
		isLastPage = bottomData.size != ITEMS_PER_PAGE
		
		startPos = data.size
		data.addAll(bottomData)
		itemsLoaded += bottomData.size
		notifyItemRangeInserted(startPos, bottomData.size)
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			val shouldBeRemovedCount = data.size - OPTIMAL_ITEMS_COUNT
			data = data.drop(shouldBeRemovedCount).toMutableList()
			notifyItemRangeRemoved(FIRST_POS, shouldBeRemovedCount)
		}
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
		
		fun bind(item: MatchedUserItem) {
			if (adapterPosition > 5 && adapterPosition == (data.size - 2) && !isLastPage)
				loadNextListener?.invoke(data.last().matchedDate, pageBottom + 1)
			
			if (itemsLoaded >= data.size && adapterPosition == 1 && pageTop > 0)
				loadPrevListener?.invoke(pageTop - 1)
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
	
}