/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.03.20 18:25
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
import com.mmdev.roove.databinding.FragmentSettingsPhotoItemBinding

/**
 * better to use notifyItemInserted instead of notifyDataSetChanged()
 * but bug with custom layout manager exists
 */

class SettingsUserPhotoAdapter (private var photosUrlsList: MutableList<String>):
		RecyclerView.Adapter<SettingsUserPhotoAdapter.SettingsPhotoViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		SettingsPhotoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                                R.layout.fragment_settings_photo_item,
		                                                parent,
		                                                false))


	override fun onBindViewHolder(viewHolder: SettingsPhotoViewHolder, position: Int) =
		viewHolder.bind(photosUrlsList[position])


	override fun getItemCount() = photosUrlsList.size

	fun updateData(newPhotoUrls: List<String>) {
		photosUrlsList = newPhotoUrls.toMutableList()
		notifyDataSetChanged()
	}

	inner class SettingsPhotoViewHolder(private val binding: FragmentSettingsPhotoItemBinding):
			RecyclerView.ViewHolder(binding.root) {

		fun bind(photoUrl: String) {
			binding.photoUrl = photoUrl
			binding.executePendingBindings()
		}

	}

}