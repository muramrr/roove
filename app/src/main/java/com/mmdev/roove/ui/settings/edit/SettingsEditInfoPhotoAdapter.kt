/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 17:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.data.PhotoItem
import com.mmdev.roove.BR
import com.mmdev.roove.databinding.FragmentSettingsEditInfoPhotoItemBinding
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter

/**
 * This is the documentation block about the class
 */

class SettingsEditInfoPhotoAdapter(private var photosList: MutableList<PhotoItem> = mutableListOf()):
		RecyclerView.Adapter<SettingsEditInfoPhotoAdapter.SettingsEditPhotoViewHolder>(),
		BaseRecyclerAdapter.BindableAdapter<MutableList<PhotoItem>> {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		SettingsEditPhotoViewHolder(
			FragmentSettingsEditInfoPhotoItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)


	override fun onBindViewHolder(holder: SettingsEditPhotoViewHolder, position: Int) =
		holder.bind(photosList[position])

	override fun getItemCount() = photosList.size
	
	fun getItem(position: Int) = photosList[position]
	
	fun removeAt(position: Int) {
		photosList.removeAt(position)
		notifyItemRemoved(position)
	}
	
	override fun setData(data: MutableList<PhotoItem>) {
		photosList = data.toMutableList()
		notifyDataSetChanged()
	}
	
	private var clickListener: ((PhotoItem, Int) -> Unit)? = null
	
	// allows clicks events to be caught
	fun setOnItemClickListener(listener: (PhotoItem, Int) -> Unit) {
		clickListener = listener
	}


	inner class SettingsEditPhotoViewHolder(private val binding: FragmentSettingsEditInfoPhotoItemBinding):
			RecyclerView.ViewHolder(binding.root) {

		fun bind(bindItem: PhotoItem) = binding.run {
			btnDeletePhoto.setOnClickListener { clickListener?.invoke(bindItem, adapterPosition) }
			
			setVariable(BR.bindItem, bindItem)
			executePendingBindings()
		}

	}
}