package com.mmdev.meetapp.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.meetapp.R
import com.mmdev.meetapp.models.FeedItem
import com.mmdev.meetapp.ui.main.view.MainActivity
import java.util.*

class FeedFragmentPage: Fragment(R.layout.fragment_feed_item) {

	private lateinit var mMainActivity: MainActivity
	private lateinit var rvFeedList: RecyclerView
	private var mFeedRecyclerAdapter: FeedRecyclerAdapter = FeedRecyclerAdapter(listOf())
	private val mFeedItems = ArrayList<FeedItem>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		if (activity != null) mMainActivity = activity as MainActivity
		rvFeedList = view.findViewById(R.id.content_main_rv_feed)
		initFeeds()
	}

	private fun initFeeds() {
		mFeedItems.addAll(FeedManager.generateDummyFeeds())
		mFeedRecyclerAdapter.updateData(mFeedItems)
		val gridLayoutManager = GridLayoutManager(rvFeedList.context, 2)
		val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
		rvFeedList.apply {
			adapter = mFeedRecyclerAdapter
			layoutManager = staggeredGridLayoutManager
			itemAnimator = DefaultItemAnimator()
		}

		rvFeedList.addOnScrollListener(object: EndlessRecyclerViewScrollListener(gridLayoutManager) {
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
