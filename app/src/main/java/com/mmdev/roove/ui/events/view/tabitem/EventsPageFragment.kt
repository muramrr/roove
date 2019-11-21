/*
 * Created by Andrii Kovalchuk on 21.11.19 21:02
 * Copyright (c) 2019. All rights reserved.
 * Last modified 21.11.19 20:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.events.view.tabitem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.events.misc.EndlessRecyclerViewScrollListener
import com.mmdev.roove.ui.events.viewmodel.EventsViewModel
import com.mmdev.roove.ui.main.view.MainActivity
import com.mmdev.roove.utils.models.FeedItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


class EventsPageFragment: Fragment(R.layout.fragment_events_page_item) {

	private lateinit var mMainActivity: MainActivity
	private lateinit var rvFeedList: RecyclerView
	private var mEventsRecyclerAdapter: EventsRecyclerAdapter = EventsRecyclerAdapter(listOf())

	private val eventsVMFactory = injector.eventsVMFactory()
	private lateinit var eventsViewModel: EventsViewModel

	private val disposables = CompositeDisposable()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }
		eventsViewModel= ViewModelProvider(this, eventsVMFactory).get(EventsViewModel::class.java)

		disposables.add(eventsViewModel.getEvents()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           //mMainActivity.showToast("${it.results[0]}")
                           mEventsRecyclerAdapter.updateData(it.results)
                       },
                       {
                           mMainActivity.showToast("$it")
                       }))
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		rvFeedList = view.findViewById(R.id.content_main_rv_feed)
		initFeeds()


		mEventsRecyclerAdapter.setOnItemClickListener(object: EventsRecyclerAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {
				mMainActivity.eventItem = mEventsRecyclerAdapter.getEventItem(position)

				mMainActivity.startEventDetailedFragment()
			}
		})
	}

	private fun initFeeds() {
		val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
		rvFeedList.apply {
			adapter = mEventsRecyclerAdapter
			layoutManager = staggeredGridLayoutManager
			itemAnimator = DefaultItemAnimator()
		}

		rvFeedList.addOnScrollListener(object: EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
			override fun onLoadMore(page: Int, totalItemsCount: Int) {
				//loadMoreFeeds()
			}
		})
	}

//	private fun loadMoreFeeds() {
//		rvFeedList.post {
//			mFeedItems.addAll(generateDummyFeeds())
//			mEventsRecyclerAdapter.notifyItemInserted(mFeedItems.size - 1)
//		}
//	}

	private fun generateDummyFeeds(): List<FeedItem> {
		val feedItems: ArrayList<FeedItem> = ArrayList()
		feedItems.add(FeedItem("Wan Clem",
		                       R.drawable.feed_content_driving_a_car,
		                       "Posted",
		                       "2hr",
		                       "https://images.unsplash.com/photo-1569183839911-5a9e0ef9c74e?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max",
		                       "A very nice mercedez Benz",
		                       15))
		feedItems.add(FeedItem("Sean Parker",
		                       R.drawable.feed_content_man_in_suit,
		                       "Shared",
		                       "3hr",
		                       "",
		                       "Men with class",
		                       19))
		feedItems.add(FeedItem("Ivanka TimberLake",
		                       R.drawable.feed_content_girl_jogging,
		                       "Shared",
		                       "4hr",
		                       "https://images.unsplash.com/photo-1568841228566-455c6533e892?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max",
		                       "Awesome feed_content_joggers",
		                       74))
		feedItems.add(FeedItem("Angelina Blanca",
		                       R.drawable.feed_content_descent,
		                       "Posted",
		                       "5hr",
		                       "",
		                       "Nice pair of feed_content_shoes",
		                       18))
		feedItems.add(FeedItem("Bradly Gates",
		                       R.drawable.feed_content_riding_bycle,
		                       "Posted",
		                       "6hr",
		                       "https://images.unsplash.com/photo-1568481694572-585cff34ee84?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max",
		                       "A very nice power bike",
		                       15))
		feedItems.add(FeedItem("Sean Parker",
		                       R.drawable.feed_content_man_in_suit,
		                       "Shared",
		                       "3hr",
		                       "",
		                       "I was born in an empty sea, My tears created oceans Producing tsunami waves with emotions Patrolling the open seas of an unknown galaxy I was floating in front of who I am physically Spiritually paralyzing mind body and soul It gives me energy when I'm lyrically exercising I gotta spit 'til the story is told in a dream by celestial bodies Follow me baby",
		                       19))
		feedItems.add(FeedItem("Sean Parker",
		                       R.drawable.feed_content_man_in_suit,
		                       "Shared",
		                       "3hr",
		                       "",
		                       "Men with class",
		                       19))
		feedItems.add(FeedItem("Sean Parker",
		                       R.drawable.feed_content_man_in_suit,
		                       "Shared",
		                       "3hr",
		                       "",
		                       "Men with class",
		                       19))
		feedItems.add(FeedItem("Sean Parker",
		                       R.drawable.feed_content_man_in_suit,
		                       "Shared",
		                       "3hr",
		                       "",
		                       "Men with class",
		                       19))
		feedItems.add(FeedItem("Sean Parker",
		                       R.drawable.feed_content_man_in_suit,
		                       "Shared",
		                       "3hr",
		                       "",
		                       "Men with class",
		                       19))
		feedItems.add(FeedItem("Sean Parker",
		                       R.drawable.feed_content_man_in_suit,
		                       "Shared",
		                       "3hr",
		                       "",
		                       "Men with class",
		                       19))
		return feedItems
	}


}
