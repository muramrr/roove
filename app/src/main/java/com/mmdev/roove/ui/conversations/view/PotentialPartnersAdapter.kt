package com.mmdev.roove.ui.conversations.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mmdev.business.cards.model.CardItem
import com.mmdev.roove.R

/* Created by A on 29.10.2019.*/

/**
 * This is the documentation block about the class
 */


class PotentialPartnersAdapter (private var mPotentialPartnersList: List<CardItem>):

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

	fun updateData(partnersList: List<CardItem>) {
		mPotentialPartnersList = partnersList
		notifyDataSetChanged()
	}

	fun getPotentialPartnerItem(position: Int): CardItem {
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

		private val ivUserAvatar: ImageView = itemView
			.findViewById(R.id.conversation_potential_item_userpic_iv)
		private val ivIndicator: ImageView = itemView
			.findViewById(R.id.conversation_potential_item_indicator_iv)

		private val tvName: TextView = itemView.findViewById(R.id.conversation_potential_item_name_tv)

		fun bind(userItem: CardItem){
			setUserAvatar(userItem.mainPhotoUrl)
			setUserName(userItem.name)
		}

		private fun setUserAvatar(url: String){
			//neon green color indicator
			Glide.with(ivIndicator.context)
				.load(ColorDrawable(Color.parseColor("#39ff14")))
				.apply(RequestOptions().circleCrop())
				.into(ivIndicator)

			Glide.with(ivUserAvatar.context)
				.load(url)
				.fallback(R.drawable.default_avatar)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.into(ivUserAvatar)
		}

		private fun setUserName(name: String){ tvName.text = name.split(" ")[0]}
	}


	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}
}