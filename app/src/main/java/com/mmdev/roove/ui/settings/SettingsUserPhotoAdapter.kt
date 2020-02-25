/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.02.20 18:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp

/**
 * This is the documentation block about the class
 */

class SettingsUserPhotoAdapter (private var photosUrlsList: List<String>):
		RecyclerView.Adapter<SettingsUserPhotoAdapter.SettingsPhotoViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		SettingsPhotoViewHolder(LayoutInflater.from(parent.context)
			                    .inflate(R.layout.fragment_settings_photo_item,
			                             parent,
			                             false))


	override fun onBindViewHolder(viewHolder: SettingsPhotoViewHolder, position: Int) {
		viewHolder.bind(photosUrlsList[position])
	}


	override fun getItemCount() = photosUrlsList.size

	fun updateData(newPhotoUrls: List<String>) {
		photosUrlsList = newPhotoUrls
		notifyDataSetChanged()
	}


	inner class SettingsPhotoViewHolder(view: View): RecyclerView.ViewHolder(view) {

		private val ivUserPhoto: ImageView = itemView.findViewById(R.id.ivUserItemPhoto)

		fun bind(photoUrl: String) {

			GlideApp.with(ivUserPhoto.context)
				.load(photoUrl)
				.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
				.placeholder(R.drawable.placeholder_profile)
				.into(ivUserPhoto)

		}

	}



}