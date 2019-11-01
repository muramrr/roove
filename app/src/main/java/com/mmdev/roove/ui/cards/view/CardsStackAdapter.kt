package com.mmdev.roove.ui.cards.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.cards.model.CardItem
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp

class CardsStackAdapter (private var cardsList: List<CardItem>):
		RecyclerView.Adapter<CardsStackAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_item, parent, false)
		return ViewHolder(itemView)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bindCard(cardsList[position])
		holder.itemView.setOnClickListener { v -> Toast.makeText(v.context, "clicked", Toast.LENGTH_SHORT).show() }

	}

	override fun getItemCount(): Int { return cardsList.size }

	override fun getItemId(position: Int): Long { return position.toLong() }

	internal fun getSwipeProfile(position: Int): CardItem { return cardsList[position] }

	fun updateData(newCardItems: List<CardItem>) {
		this.cardsList = newCardItems
		notifyDataSetChanged()
	}

	inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
		private val tvNameCard: TextView = itemView.findViewById(R.id.fragment_card_item_text_name)
		private val tvImageCard: ImageView = itemView.findViewById(R.id.fragment_card_item_img_photo)

		fun bindCard(cardItem: CardItem){
			tvNameCard.text = cardItem.name
			GlideApp.with(tvImageCard).load(cardItem.mainPhotoUrl).into(tvImageCard)
		}

	}

}
