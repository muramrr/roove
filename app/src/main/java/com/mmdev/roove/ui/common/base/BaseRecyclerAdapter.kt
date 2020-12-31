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

package com.mmdev.roove.ui.common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.BR

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding</V></T>
 */

abstract class BaseRecyclerAdapter<T>:
		RecyclerView.Adapter<BaseRecyclerAdapter<T>.BaseViewHolder<T>>() {

	private var mClickListener: OnItemClickListener<T>? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		BaseViewHolder<T>(
			DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				viewType,
				parent,
				false
			)
		)

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) =
		holder.bind(getItem(position))

	override fun getItemViewType(position: Int) = getLayoutIdForItem(position)

	abstract fun getItem(position: Int): T
	abstract fun getLayoutIdForItem(position: Int): Int

	// allows clicks events to be caught
	open fun setOnItemClickListener(itemClickListener: OnItemClickListener<T>) {
		mClickListener = itemClickListener
	}

	override fun onFailedToRecycleView(holder: BaseViewHolder<T>): Boolean { return true }

	inner class BaseViewHolder<T>(private val binding: ViewDataBinding):
			RecyclerView.ViewHolder(binding.root){

		init {
			mClickListener?.let { mClickListener ->
				itemView.setOnClickListener {
					mClickListener.onItemClick(getItem(adapterPosition), adapterPosition)
				}
			}
		}

		fun bind(item: T) {
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener<T> {
		fun onItemClick(item: T, position: Int)
	}

	interface BindableAdapter<T> {
		fun setData(data: T)
	}
}