package com.mmdev.meetapp.ui.cards.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.domain.core.model.User
import com.mmdev.meetapp.R
import com.mmdev.meetapp.utils.GlideApp

class CardsStackAdapter internal constructor(private val mUsersList: List<User>):
		RecyclerView.Adapter<CardsStackAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_item, parent, false)
		return ViewHolder(itemView)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val (name, city, _, _, mainPhotoUrl) = mUsersList[position]
		holder.tvNameCard.text = name
		holder.tvCityCard.text = city
		GlideApp.with(holder.tvImageCard).load(mainPhotoUrl).into(holder.tvImageCard)
		holder.itemView.setOnClickListener { v -> Toast.makeText(v.context, "clicked", Toast.LENGTH_SHORT).show() }

	}

	override fun getItemCount(): Int { return mUsersList.size }

	override fun getItemId(position: Int): Long { return position.toLong() }

	internal fun getSwipeProfile(position: Int): User { return mUsersList[position] }

	inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
		internal val tvNameCard: TextView = itemView.findViewById(R.id.fragment_card_item_text_name)
		internal val tvCityCard: TextView = itemView.findViewById(R.id.fragment_card_item_text_city)
		internal val tvImageCard: ImageView = itemView.findViewById(R.id.fragment_card_item_img_photo)

	}

}
