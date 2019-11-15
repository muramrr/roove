package com.mmdev.roove.ui.cards.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.cards.model.CardItem
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp

class CardsStackAdapter (private var cardsList: List<CardItem>):
		RecyclerView.Adapter<CardsStackAdapter.ViewHolder>() {

	private lateinit var clickListener: OnItemClickListener

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_item,
		                                                       parent,
		                                                       false))


	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bindCard(cardsList[position])
	}

	override fun getItemCount(): Int { return cardsList.size }

	fun getCard(position: Int) = cardsList[position]

	fun updateData(newCardItems: List<CardItem>) {
		this.cardsList = newCardItems
		notifyDataSetChanged()
	}

	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		clickListener = itemClickListener
	}

	inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

		init {
			itemView.setOnClickListener {
				clickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		private val tvNameCard: TextView = itemView.findViewById(R.id.fragment_card_item_text_name)
		private val tvImageCard: ImageView = itemView.findViewById(R.id.fragment_card_item_img_photo)

		fun bindCard(cardItem: CardItem){
			tvNameCard.text = cardItem.name
			GlideApp.with(tvImageCard).load(cardItem.mainPhotoUrl).into(tvImageCard)
		}

	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}

}
