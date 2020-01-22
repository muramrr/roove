/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 18:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.tabitem

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.places.PlacesViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_places_page_item.*


class PlacesPageFragment: BaseFragment(R.layout.fragment_places_page_item) {

	private var mPlacesRecyclerAdapter = PlacesRecyclerAdapter(listOf())

	private var receivedCategory = ""

	private lateinit var placesViewModel: PlacesViewModel


	companion object {

		private const val CATEGORY_KEY = "CATEGORY"

		fun newInstance(category: String) = PlacesPageFragment().apply {
			arguments = Bundle().apply {
				putString(CATEGORY_KEY, category)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		arguments?.let {
			receivedCategory = it.getString(CATEGORY_KEY, "")
		}

		placesViewModel = ViewModelProvider(this, factory)[PlacesViewModel::class.java]
		placesViewModel.loadPlaces(receivedCategory)
		placesViewModel.getPlacesList().observe(this, Observer {
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

				val placeId = bundleOf("PLACE_ID" to
						                      mPlacesRecyclerAdapter.getPlaceItem(position).id)

				findNavController().navigate(R.id.action_places_to_placeDetailedFragment, placeId)

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
