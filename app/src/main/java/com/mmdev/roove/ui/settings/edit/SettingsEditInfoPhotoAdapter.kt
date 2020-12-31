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