/*
 * Created by Andrii Kovalchuk on 04.10.19 16:23
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.11.19 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.profile.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentProfilePagerItemBinding

/**
 * This is the documentation block about the class
 */

class ProfilePagerAdapter (private var listPhotoUrls: List<String>):
		RecyclerView.Adapter<ProfilePagerAdapter.ProfileImagesHolder>() {


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ProfileImagesHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                            R.layout.fragment_profile_pager_item,
		                                            parent,
		                                            false))


	override fun onBindViewHolder(holder: ProfileImagesHolder, position: Int) =
		holder.bind(listPhotoUrls[position])

	override fun getItemCount() = listPhotoUrls.size

	fun updateData(newPhotosUrlList: List<String>) {
		listPhotoUrls = newPhotosUrlList
		notifyDataSetChanged()
	}


	inner class ProfileImagesHolder (private val binding: FragmentProfilePagerItemBinding) :
			RecyclerView.ViewHolder(binding.root) {

		fun bind(photoUrl: String) {
			binding.photoUrl = photoUrl
		}

	}


}

