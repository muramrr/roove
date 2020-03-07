/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 19:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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

abstract class BaseAdapter<T>:
		RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder<T>>() {

	private var mClickListener: OnItemClickListener<T>? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		BaseViewHolder<T>(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                          viewType,
		                                          parent,
		                                          false))

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) =
		holder.bind(getItem(position))

	override fun getItemViewType(position: Int) = getLayoutIdForItem(position)

	abstract fun getItem(position: Int): T
	abstract fun getLayoutIdForItem(position: Int): Int

	// allows clicks events to be caught
	open fun setOnItemClickListener(itemClickListener: OnItemClickListener<T>) {
		mClickListener = itemClickListener
	}

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