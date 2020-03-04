/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.03.20 19:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.core.PhotoItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentSettingsEditInfoPhotoItemBinding
import kotlinx.android.synthetic.main.fragment_settings_edit_info_photo_item.view.*

/**
 * This is the documentation block about the class
 */

class SettingsEditInfoPhotoAdapter (private var photosList: MutableList<PhotoItem>):
		RecyclerView.Adapter<SettingsEditInfoPhotoAdapter.SettingsEditPhotoViewHolder>() {

	private lateinit var mClickListener: OnItemClickListener

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		SettingsEditPhotoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                                    R.layout.fragment_settings_edit_info_photo_item,
		                                                    parent,
		                                                false))


	override fun onBindViewHolder(viewHolder: SettingsEditPhotoViewHolder, position: Int) =
		viewHolder.bind(photosList[position].fileUrl)


	override fun getItemCount() = photosList.size

	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		mClickListener = itemClickListener
	}

	fun getPhoto(position: Int) = photosList[position]

	fun removeAt(position: Int) {
		photosList.removeAt(position)
		notifyItemRemoved(position)
	}

	fun updateData(newPhotoUrls: List<PhotoItem>) {
		photosList = newPhotoUrls.toMutableList()
		notifyDataSetChanged()
	}

	inner class SettingsEditPhotoViewHolder(private val binding: FragmentSettingsEditInfoPhotoItemBinding):
			RecyclerView.ViewHolder(binding.root) {

		init {
			itemView.btnDeletePhoto.setOnClickListener {
				mClickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		fun bind(photoUrl: String) {
			binding.photoUrl = photoUrl
			binding.executePendingBindings()
		}

	}

	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}
}