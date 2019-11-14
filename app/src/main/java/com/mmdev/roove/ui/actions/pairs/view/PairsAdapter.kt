package com.mmdev.roove.ui.actions.pairs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.business.cards.model.CardItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPairsItemBinding


/* Created by A on 13.11.2019.*/

/**
 * This is the documentation block about the class
 */

class PairsAdapter (private var mPairsList: List<CardItem>):
		RecyclerView.Adapter<PairsAdapter.PairsViewHolder>() {


	private lateinit var clickListener: OnItemClickListener


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PairsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                                R.layout.fragment_pairs_item,
		                                                parent,
		                                                false))

	override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
		holder.bind(mPairsList[position])
	}

	override fun getItemCount() = mPairsList.size

	fun updateData(conversations: List<CardItem>) {
		mPairsList = conversations
		notifyDataSetChanged()
	}

	fun getPairItem(position: Int): CardItem{ return mPairsList[position] }

	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		clickListener = itemClickListener
	}


	inner class PairsViewHolder (private val binding: FragmentPairsItemBinding):
			RecyclerView.ViewHolder(binding.root) {


		init {
			itemView.setOnClickListener {
				clickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		fun bind(matchedItem: CardItem){
			binding.matchedItem = matchedItem
			binding.executePendingBindings()
		}
	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}
}