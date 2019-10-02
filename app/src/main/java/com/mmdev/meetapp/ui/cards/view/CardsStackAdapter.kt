package com.mmdev.meetapp.ui.cards.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.user.model.User
import com.mmdev.meetapp.R
import com.mmdev.meetapp.core.GlideApp

class CardsStackAdapter (private var usersList: List<User>):
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

	internal fun getSwipeProfile(position: Int): User { return usersList[position] }

	fun updateData(newUsers: List<User>) {
		this.usersList = newUsers
		notifyDataSetChanged()
	}

	inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
		private val tvNameCard: TextView = itemView.findViewById(R.id.fragment_card_item_text_name)
		private val tvCityCard: TextView = itemView.findViewById(R.id.fragment_card_item_text_city)
		private val tvImageCard: ImageView = itemView.findViewById(R.id.fragment_card_item_img_photo)

		fun bindCard(user: User){
			tvNameCard.text = user.name
			tvCityCard.text = user.city
			GlideApp.with(tvImageCard).load(user.mainPhotoUrl).into(tvImageCard)
		}

	}

}
