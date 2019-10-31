package com.mmdev.roove.ui.conversations.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mmdev.business.user.model.UserItem
import com.mmdev.roove.R

/* Created by A on 29.10.2019.*/

/**
 * This is the documentation block about the class
 */





/* Created by A on 27.10.2019.*/

/**
 * This is the documentation block about the class
 */

class PotentialPartnersAdapter (private var mPotentialPartnersList: List<UserItem>):

		RecyclerView.Adapter<PotentialPartnersAdapter.PotentialPartnersViewHolder>() {

	private lateinit var clickListener: OnItemClickListener

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PotentialPartnersViewHolder(LayoutInflater.from(parent.context)
			                                 .inflate(R.layout.fragment_conversation_potential_item,
			                                          parent,
			                                          false))

	override fun onBindViewHolder(holder: PotentialPartnersViewHolder, position: Int) {
		holder.bind(mPotentialPartnersList[position])
	}

	override fun getItemCount(): Int { return mPotentialPartnersList.size }

	fun updateData(partnersList: List<UserItem>) {
		mPotentialPartnersList = partnersList
		notifyDataSetChanged()
	}

	fun getPotentialPartnerItem(position: Int): UserItem {
		return mPotentialPartnersList[position]
	}

	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		clickListener = itemClickListener
	}

	inner class PotentialPartnersViewHolder(view: View) : RecyclerView.ViewHolder(view){

		init {
			itemView.setOnClickListener {
				clickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		//potential user conversation
		private val ivUserAvatar: ImageView = itemView
			.findViewById(R.id.conversation_potential_item_userpic_iv)

		private val ivIndicator: ImageView = itemView
			.findViewById(R.id.conversation_potential_item_indicator_iv)

		fun bind(userItem: UserItem){
			setUserAvatar(userItem.mainPhotoUrl)
		}

		private fun setUserAvatar(url: String){
//			Glide.with(ivIndicator.context)
//				.load(Color)
//				.apply(RequestOptions().circleCrop())
//				.into(ivIndicator)

			Glide.with(ivUserAvatar.context)
				.load(url)
				.fallback(R.drawable.default_avatar)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.into(ivUserAvatar)
		}
	}


	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}
}