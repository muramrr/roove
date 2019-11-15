package com.mmdev.roove.ui.feed.tabitem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentFeedRvItemBinding
import com.mmdev.roove.utils.models.FeedItem


class FeedRecyclerAdapter (private var mFeedItems: List<FeedItem>):
		RecyclerView.Adapter<FeedRecyclerAdapter.FeedItemHolder>() {

	private lateinit var mClickListener: OnItemClickListener

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		FeedItemHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
		                                       R.layout.fragment_feed_rv_item,
			                                   parent,
			                                   false))


	override fun onBindViewHolder(holder: FeedItemHolder, position: Int) =
		holder.bind(mFeedItems[position])


	override fun getItemCount() = mFeedItems.size

	fun updateData(feed: List<FeedItem>) {
		mFeedItems = feed
		notifyDataSetChanged()
	}

	fun getFeedItem(position: Int) = mFeedItems[position]


	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
		mClickListener = itemClickListener
	}


	inner class FeedItemHolder(private val binding: FragmentFeedRvItemBinding) :
			RecyclerView.ViewHolder(binding.root) {

		init {
			itemView.setOnClickListener {
				mClickListener.onItemClick(itemView.rootView, adapterPosition)
			}
		}

		//feed content
		private val ivFeedPhoto: ImageView = itemView.findViewById(R.id.feed_content_image_view)

		fun bind(feedItem: FeedItem) {
			binding.feedItem = feedItem
			setFeedPhoto(feedItem.feedContentImageView)
			binding.executePendingBindings()
		}


		private fun setFeedPhoto(url: String){
			if (url.isNotEmpty())
				Glide.with(ivFeedPhoto.context)
					.load(url)
					.into(ivFeedPhoto)
			else ivFeedPhoto.visibility = View.GONE
		}

	}

	// parent fragment will override this method to respond to click events
	interface OnItemClickListener {
		fun onItemClick(view: View, position: Int)
	}

}
