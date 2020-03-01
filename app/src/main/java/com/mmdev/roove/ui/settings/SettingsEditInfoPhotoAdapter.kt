/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.03.20 19:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentSettingsEditInfoPhotoItemBinding

/**
 * This is the documentation block about the class
 */

class SettingsEditInfoPhotoAdapter (private var photosUrlsList: MutableList<String>):
		RecyclerView.Adapter<SettingsEditInfoPhotoAdapter.SettingsEditPhotoViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		SettingsEditPhotoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                                    R.layout.fragment_settings_edit_info_photo_item,
		                                                    parent,
		                                                false))


	override fun onBindViewHolder(viewHolder: SettingsEditPhotoViewHolder, position: Int) =
		viewHolder.bind(photosUrlsList[position])


	override fun getItemCount() = photosUrlsList.size

	fun removeAt(position: Int) {
		photosUrlsList.removeAt(position)
		notifyItemRemoved(position)
	}

	fun updateData(newPhotoUrls: List<String>) {
		photosUrlsList = newPhotoUrls.toMutableList()
		notifyDataSetChanged()
	}

	inner class SettingsEditPhotoViewHolder(private val binding: FragmentSettingsEditInfoPhotoItemBinding):
			RecyclerView.ViewHolder(binding.root) {

		fun bind(photoUrl: String) {
			binding.photoUrl = photoUrl
			binding.executePendingBindings()
		}

	}

}