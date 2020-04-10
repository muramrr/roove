/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.04.20 17:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places.view.tabitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.mmdev.business.places.PlaceItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentPlacesPageItemBinding
import com.mmdev.roove.ui.common.base.BaseAdapter
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.GridItemDecoration
import com.mmdev.roove.ui.places.PlacesViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_places_page_item.*


class PlacesPageFragment: BaseFragment<PlacesViewModel>() {

	private var mPlacesRecyclerAdapter = PlacesRecyclerAdapter(layoutId = R.layout.fragment_places_page_rv_item)

	private var receivedCategoryResource = 0
	private var receivedCategoryString = ""

	companion object {

		private const val CATEGORY_KEY = "CATEGORY"
		private const val PLACE_ID_KEY = "PLACE_ID"

		fun newInstance(category: Int) = PlacesPageFragment().apply {
			arguments = Bundle().apply {
				putInt(CATEGORY_KEY, category)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		associatedViewModel = getViewModel()
		arguments?.let {
			receivedCategoryResource = it.getInt(CATEGORY_KEY, 0)
			receivedCategoryString = getString(receivedCategoryResource)
		}

		associatedViewModel.loadFirstPlaces(receivedCategoryString)

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentPlacesPageItemBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@PlacesPageFragment
				viewModel = associatedViewModel
				executePendingBindings()
			}.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
		rvPlacesList.apply {
			adapter = mPlacesRecyclerAdapter
			layoutManager = gridLayoutManager
			addItemDecoration(GridItemDecoration())

			addOnScrollListener(object: EndlessRecyclerViewScrollListener(gridLayoutManager) {
				override fun onLoadMore(page: Int, totalItemsCount: Int) {

					if (gridLayoutManager.findLastCompletelyVisibleItemPosition() <= totalItemsCount - 4){
						associatedViewModel.loadMorePlaces(receivedCategoryString)
					}

				}
			})
		}

		mPlacesRecyclerAdapter.setOnItemClickListener(object: BaseAdapter.OnItemClickListener<PlaceItem> {

			override fun onItemClick(item: PlaceItem, position: Int) {
				val placeId = bundleOf(PLACE_ID_KEY to item.id)
				navController.navigate(R.id.action_places_to_placeDetailedFragment, placeId)
			}
		})
	}


}
