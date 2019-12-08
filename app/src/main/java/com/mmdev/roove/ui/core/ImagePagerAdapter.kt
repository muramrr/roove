/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 08.12.19 20:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R
import com.mmdev.roove.databinding.UniversalPagerImageContainerBinding

/**
 * This is the documentation block about the class
 */

class ImagePagerAdapter (private var imagesUrlList: List<String>):
		RecyclerView.Adapter<ImagePagerAdapter.ImagePagerHolder>() {


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ImagePagerHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                         R.layout.universal_pager_image_container,
		                                         parent,
		                                         false))

	override fun onBindViewHolder(pagerHolder: ImagePagerHolder, position: Int) =
		pagerHolder.bind(imagesUrlList[position])

	override fun getItemCount() = imagesUrlList.size

	fun updateData(newImagesList: List<String>) {
		imagesUrlList = newImagesList
		notifyDataSetChanged()
	}

	inner class ImagePagerHolder(private val binding: UniversalPagerImageContainerBinding) :
			RecyclerView.ViewHolder(binding.root) {


		/*
		*   executePendingBindings()
		*   Evaluates the pending bindings,
		*   updating any Views that have expressions bound to modified variables.
		*   This must be run on the UI thread.
		*/
		fun bind(imageUrl: String){
			if (imageUrl.isNotEmpty()) {
				binding.imageUrl = imageUrl
				binding.executePendingBindings()
			}
		}

	}

}