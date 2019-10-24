package com.mmdev.roove.ui.feed.tabitem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.roove.R
import com.mmdev.roove.models.FeedItem
import com.mmdev.roove.ui.feed.misc.EndlessRecyclerViewScrollListener
import com.mmdev.roove.ui.feed.misc.FeedManager
import com.mmdev.roove.ui.main.view.MainActivity
import java.util.*

class FeedPageFragment: Fragment(R.layout.fragment_feed_page_item) {

	private lateinit var mMainActivity: MainActivity
	private lateinit var rvFeedList: RecyclerView
	private var mFeedRecyclerAdapter: FeedRecyclerAdapter = FeedRecyclerAdapter(listOf())
	private val mFeedItems = ArrayList<FeedItem>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		if (activity != null) mMainActivity = activity as MainActivity
		rvFeedList = view.findViewById(R.id.content_main_rv_feed)
		initFeeds()


		mFeedRecyclerAdapter.setOnItemClickListener(object: FeedRecyclerAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {
				val intent = Intent(mMainActivity, FeedItemActivity::class.java)
				intent.putExtra("feedId", mFeedRecyclerAdapter.getFeedItem(position).feedPublisherName)
				startActivity(intent)

			}
		})
	}

	private fun initFeeds() {
		mFeedItems.addAll(FeedManager.generateDummyFeeds())
		mFeedRecyclerAdapter.updateData(mFeedItems)
		val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
		rvFeedList.apply {
			adapter = mFeedRecyclerAdapter
			layoutManager = staggeredGridLayoutManager
			itemAnimator = DefaultItemAnimator()
		}

		rvFeedList.addOnScrollListener(object: EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
			override fun onLoadMore(page: Int, totalItemsCount: Int) {
				loadMoreFeeds()
			}
		})
	}

	private fun loadMoreFeeds() {
		rvFeedList.post {
			mFeedItems.addAll(FeedManager.generateDummyFeeds())
			mFeedRecyclerAdapter.notifyItemInserted(mFeedItems.size - 1)
		}
	}




}
