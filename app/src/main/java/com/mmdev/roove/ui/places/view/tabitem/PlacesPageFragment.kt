/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.01.20 21:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.tabitem

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.places.PlacesViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_places_page_item.*


class PlacesPageFragment: BaseFragment(R.layout.fragment_places_page_item) {

	private var mPlacesRecyclerAdapter = PlacesRecyclerAdapter(listOf())


	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var placesViewModel: PlacesViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")


		placesViewModel = ViewModelProvider(this, factory)[PlacesViewModel::class.java]
		placesViewModel.loadPlaces()
		placesViewModel.getEventsList().observe(this, Observer {
			mPlacesRecyclerAdapter.updateData(it)
		})
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val staggeredLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
		rvPlacesList.apply {
			adapter = mPlacesRecyclerAdapter
			layoutManager = staggeredLayoutManager
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(staggeredLayoutManager) {
				override fun onLoadMore(page: Int, totalItemsCount: Int) {
					//loadMoreFeeds()
				}
			})
		}

		mPlacesRecyclerAdapter.setOnItemClickListener(object: PlacesRecyclerAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {

				sharedViewModel.setPlaceSelected(mPlacesRecyclerAdapter.getPlaceItem(position))

				findNavController().navigate(R.id.action_places_to_placeDetailedFragment)

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
