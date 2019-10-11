package com.mmdev.meetapp.ui.feed.tabitem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mmdev.meetapp.R
import com.mmdev.meetapp.models.FeedItem






class FeedRecyclerAdapter (private var mFeedItems: List<FeedItem>):
		RecyclerView.Adapter<FeedRecyclerAdapter.FeedItemHolder>() {

	private lateinit var mClickListener: OnItemClickListener

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemHolder =
		FeedItemHolder(LayoutInflater.from(parent.context)
			               .inflate(R.layout.fragment_feed_rv_item, parent, false))


	override fun onBindViewHolder(holder: FeedItemHolder, position: Int) {
		holder.bindItem(mFeedItems[position])
	}

	override fun getItemCount(): Int { return mFeedItems.size }

	fun updateData(feed: List<FeedItem>) {
		mFeedItems = feed
		notifyDataSetChanged()
	}

	fun getFeedItem(position: Int): FeedItem{
		return mFeedItems[position]
	}


	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		mClickListener = itemClickListener
	}


	inner class FeedItemHolder(view: View) : RecyclerView.ViewHolder(view) {


		init {
			itemView.setOnClickListener {
				mClickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		//publisher user
		private val tvFeedUserName: TextView = itemView.findViewById(R.id.feed_publisher_name_view)
		private val tvFeedTimestamp: TextView = itemView.findViewById(R.id.feed_publish_date)
		private val ivFeedUserAvatar: ImageView = itemView.findViewById(R.id.feed_publisher_image_view)
		//feed content
		private val tvFeedTextMessage: TextView = itemView.findViewById(R.id.feed_content_description)
		private val ivFeedPhoto: ImageView = itemView.findViewById(R.id.feed_content_image_view)

		fun bindItem(feedItem: FeedItem) {
			tvFeedUserName.text = feedItem.feedPublisherName
			tvFeedTimestamp.text = feedItem.feedSharedTime
			setUserAvatar(feedItem.feedPublisherPhotoId)
			tvFeedTextMessage.text = feedItem.feedContentDescription
			setFeedPhoto(feedItem.feedContentImageView)
		}

		fun getAdapterPostition(): FeedItem {
			return mFeedItems[adapterPosition]
		}

		private fun setUserAvatar(url: Int){
			Glide.with(ivFeedUserAvatar.context)
				.load(url)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.into(ivFeedUserAvatar)
		}

		private fun setFeedPhoto(url: String){
			if (url.isNotEmpty())
				Glide.with(ivFeedPhoto.context)
					.load(url)
					.into(ivFeedPhoto)
			else ivFeedPhoto.visibility = View.GONE
		}

	}



	// parent activity will implement this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}

}
