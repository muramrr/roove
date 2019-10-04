package com.mmdev.meetapp.ui.feed

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

	inner class FeedItemHolder(view: View) : RecyclerView.ViewHolder(view){

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
			Glide.with(ivFeedUserAvatar.context)
				.load(feedItem.feedPublisherPhotoId)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.into(ivFeedUserAvatar)
			tvFeedTextMessage.text = feedItem.feedContentDescription
			setFeedPhoto(feedItem.feedContentImageView)
		}

		private fun setFeedPhoto(url: String){
			if (url.isNotEmpty())
				Glide.with(ivFeedPhoto.context)
					.load(url)
					.into(ivFeedPhoto)
			else ivFeedPhoto.visibility = View.GONE
		}

	}

}
