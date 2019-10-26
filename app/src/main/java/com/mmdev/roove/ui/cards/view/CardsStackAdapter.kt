package com.mmdev.roove.ui.cards.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.user.model.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp

class CardsStackAdapter (private var usersList: List<UserItem>):
		RecyclerView.Adapter<CardsStackAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_item, parent, false)
		return ViewHolder(itemView)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bindCard(usersList[position])
		holder.itemView.setOnClickListener { v -> Toast.makeText(v.context, "clicked", Toast.LENGTH_SHORT).show() }

	}

	override fun getItemCount(): Int { return usersList.size }

	override fun getItemId(position: Int): Long { return position.toLong() }

	internal fun getSwipeProfile(position: Int): UserItem { return usersList[position] }

	fun updateData(newUserItems: List<UserItem>) {
		this.usersList = newUserItems
		notifyDataSetChanged()
	}

	inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
		private val tvNameCard: TextView = itemView.findViewById(R.id.fragment_card_item_text_name)
		private val tvCityCard: TextView = itemView.findViewById(R.id.fragment_card_item_text_city)
		private val tvImageCard: ImageView = itemView.findViewById(R.id.fragment_card_item_img_photo)

		fun bindCard(userItem: UserItem){
			tvNameCard.text = userItem.name
			tvCityCard.text = userItem.city
			GlideApp.with(tvImageCard).load(userItem.mainPhotoUrl).into(tvImageCard)
		}

	}

}
