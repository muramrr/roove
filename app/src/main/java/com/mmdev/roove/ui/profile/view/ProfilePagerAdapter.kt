package com.mmdev.roove.ui.profile.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp


/* Created by A on 04.10.2019.*/

/**
 * This is the documentation block about the class
 */

class ProfilePagerAdapter (private var listPhotoUrls: List<String>):
		RecyclerView.Adapter<ProfilePagerAdapter.ProfileImageHolder>() {


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ProfileImageHolder(LayoutInflater.from(parent.context)
			                   .inflate(R.layout.fragment_profile_pager_item,
			                            parent,
			                            false))


	override fun onBindViewHolder(holder: ProfileImageHolder, position: Int) {
		holder.bind(listPhotoUrls[position])
	}

	override fun getItemCount() = listPhotoUrls.size


	inner class ProfileImageHolder (view: View) : RecyclerView.ViewHolder(view) {

		private val ivProfilePhoto: ImageView = itemView
			.findViewById(R.id.profile_user_pic)

		fun bind(photoUrl: String) {
			GlideApp.with(ivProfilePhoto.context)
				.load(photoUrl)
				.into(ivProfilePhoto)
		}

	}


}

