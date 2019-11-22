/*
 * Created by Andrii Kovalchuk on 22.11.19 19:36
 * Copyright (c) 2019. All rights reserved.
 * Last modified 22.11.19 19:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.tabitem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.main.view.MainActivity
import com.mmdev.roove.ui.places.viewmodel.EventsViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


class PlacesPageFragment: Fragment(R.layout.fragment_places_page_item) {

	private lateinit var mMainActivity: MainActivity
	private lateinit var rvFeedList: RecyclerView
	private var mPlacesRecyclerAdapter: PlacesRecyclerAdapter = PlacesRecyclerAdapter(listOf())

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
                           mPlacesRecyclerAdapter.updateData(it.results)
                       },
                       {
                           mMainActivity.showToast("$it")
                       }))
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		rvFeedList = view.findViewById(R.id.content_main_rv_feed)
		initFeeds()


		mPlacesRecyclerAdapter.setOnItemClickListener(object: PlacesRecyclerAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {
				mMainActivity.eventItem = mPlacesRecyclerAdapter.getEventItem(position)

				mMainActivity.startEventDetailedFragment()
			}
		})
	}

	private fun initFeeds() {
		val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
		rvFeedList.apply {
			adapter = mPlacesRecyclerAdapter
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


}
