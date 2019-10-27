package com.mmdev.roove.ui.conversations.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentConversationItemBinding

/* Created by A on 27.10.2019.*/

/**
 * This is the documentation block about the class
 */

class ConversationsAdapter (private var mConversationsList: List<ConversationItem>):
		RecyclerView.Adapter<ConversationsAdapter.ConversationsViewHolder>() {

	private lateinit var clickListener: OnItemClickListener

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationsViewHolder =
		ConversationsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                                R.layout.fragment_conversation_item,
		                                                parent,
		                                                false))

	override fun onBindViewHolder(holder: ConversationsViewHolder, position: Int) {
		holder.bind(mConversationsList[position])
	}

	override fun getItemCount(): Int { return mConversationsList.size }

	fun updateData(conversations: List<ConversationItem>) {
		mConversationsList = conversations
		notifyDataSetChanged()
	}

	fun getFeedItem(position: Int): ConversationItem{ return mConversationsList[position] }

	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		clickListener = itemClickListener
	}

	inner class ConversationsViewHolder(private val binding: FragmentConversationItemBinding):
			RecyclerView.ViewHolder(binding.root){

		init {
			itemView.setOnClickListener {
				clickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		//publisher user
		private val ivUserAvatar: ImageView = itemView.findViewById(R.id.conversation_item_userpic_iv)

		fun bind(conversationItem: ConversationItem){
			binding.conversationItem = conversationItem
			setUserAvatar(conversationItem.partnerPhotoUrl)
			binding.executePendingBindings()
		}

		private fun setUserAvatar(url: String){
			if (url.isNotEmpty())
				Glide.with(ivUserAvatar.context)
					.load(url)
					.centerCrop()
					.apply(RequestOptions().circleCrop())
					.into(ivUserAvatar)
			else Glide.with(ivUserAvatar.context)
				.load(R.drawable.default_avatar)
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