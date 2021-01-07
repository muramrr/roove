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

package com.mmdev.roove.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmdev.domain.photo.PhotoItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.ItemSettingsPhotoBinding
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter

/**
 * better to use notifyItemInserted instead of notifyDataSetChanged()
 * but bug with custom layout manager exists
 */

class SettingsUserPhotoAdapter(
	private var data: List<PhotoItem> = emptyList()
): BaseRecyclerAdapter<PhotoItem>(){
	
	override fun getItem(position: Int): PhotoItem = data[position]
	override fun getItemCount() = data.size
	override fun getLayoutIdForItem(position: Int) = R.layout.item_settings_photo
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PhotoItem> {
		val binding = ItemSettingsPhotoBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		binding.root.post {
			binding.root.layoutParams.width = parent.width / 2
			binding.root.requestLayout()
		}
		return BaseViewHolder(binding)
	}
	
	fun newAdded(newData: PhotoItem) {
		data = data.plus(newData)
		notifyDataSetChanged()
	}
	
	fun setData(newData: List<PhotoItem>) {
		data = newData
		notifyDataSetChanged()
	}
}