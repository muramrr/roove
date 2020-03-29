/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.03.20 19:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.chat.view


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mmdev.business.chat.MessageItem
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp
import com.mmdev.roove.ui.common.base.BaseAdapter

class ChatAdapter (private var listMessageItems: List<MessageItem> = mutableListOf()):
	RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(),
	BaseAdapter.BindableAdapter<List<MessageItem>>{

	private lateinit var attachedPhotoClickListener: OnItemClickListener
	private var userId = ""

	companion object {
		private const val RIGHT_MSG = 0
		private const val LEFT_MSG = 1
		private const val RIGHT_MSG_IMG = 2
		private const val LEFT_MSG_IMG = 3
	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		if (viewType == RIGHT_MSG || viewType == RIGHT_MSG_IMG)
			ChatViewHolder(LayoutInflater.from(parent.context)
				               .inflate(R.layout.fragment_chat_item_right,
				                        parent,
				                        false))
			else ChatViewHolder(LayoutInflater.from(parent.context)
				.inflate(R.layout.fragment_chat_item_left,
				         parent,
				         false))

	override fun onBindViewHolder(viewHolder: ChatViewHolder, position: Int) {
		viewHolder.setMessageType(getItemViewType(position))
		viewHolder.bind(listMessageItems[position])
	}

	override fun getItemViewType(position: Int): Int {
		val message = listMessageItems[position]
		return if (message.photoItem != null) {
			if (message.sender.userId == userId) RIGHT_MSG_IMG
			else LEFT_MSG_IMG
		}
		else {
			if (message.sender.userId == userId) RIGHT_MSG
			else LEFT_MSG
		}
	}

	override fun getItemCount() = listMessageItems.size

	fun newMessage(position: Int = 0){
		//listMessageItems.add(position, messageItem)
		notifyItemInserted(position)
	}

	fun getItem(position: Int) = listMessageItems[position]

	fun setCurrentUserId(id: String) { userId = id }

	override fun setData(data: List<MessageItem>) {
		listMessageItems = data
		notifyDataSetChanged()
	}

	/* note: USE FOR -DEBUG ONLY */
//	fun changeSenderName(name:String){
//		userId = name
//	}

	// allows clicks events on attached photo
	fun setOnAttachedPhotoClickListener(itemClickListener: OnItemClickListener) {
		attachedPhotoClickListener = itemClickListener
	}

	inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view){

		private val tvTextMessage: TextView = itemView.findViewById(R.id.tvChatMessageText)
		private val ivChatPhoto: ImageView = itemView.findViewById(R.id.ivChatMessagePhoto)

		init {
			ivChatPhoto.setOnClickListener {
				attachedPhotoClickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		fun setMessageType(messageType: Int) {
			when (messageType) {
				RIGHT_MSG -> ivChatPhoto.visibility = View.GONE
				LEFT_MSG -> ivChatPhoto.visibility = View.GONE
				RIGHT_MSG_IMG -> tvTextMessage.visibility = View.GONE
				LEFT_MSG_IMG -> tvTextMessage.visibility = View.GONE
			}
		}

		fun bind(messageItem: MessageItem) {
			setTextMessage(messageItem.text)
			messageItem.photoItem?.let {
				if (ivChatPhoto.visibility != View.GONE) setIvChatPhoto(it.fileUrl)
			}
		}

		/* sets text message in TxtView binded layout */
		private fun setTextMessage(message: String?) { tvTextMessage.text = message }

		/* set photo that user sends in chat */
		private fun setIvChatPhoto(url: String) {
			if (url.isNotEmpty()) {
				GlideApp.with(ivChatPhoto.context)
					.load(url)
					.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
					.into(ivChatPhoto)
			}
		}

	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}

}
