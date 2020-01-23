/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.01.20 18:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.places.PlaceDetailedItem
import com.mmdev.business.places.PlaceItem
import com.mmdev.business.places.usecase.GetPlaceDetailsUseCase
import com.mmdev.business.places.usecase.GetPlacesUseCase
import com.mmdev.roove.ui.core.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PlacesViewModel @Inject constructor(private val getPlacesUC: GetPlacesUseCase,
                                          private val getPlaceDetailsUC: GetPlaceDetailsUseCase):
		BaseViewModel() {


	private val placesList: MutableLiveData<List<PlaceItem>> = MutableLiveData()
	private val placeDetailed: MutableLiveData<PlaceDetailedItem> = MutableLiveData()

	fun loadPlaces(category: String){
		disposables.add(getPlacesExecution(category)
            .retry(5)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       placesList.value = it.results
	                       Log.wtf(TAG, "$category to display: ${it.results.size}")
                       },
                       {
                           Log.wtf(TAG, "$it")
                       }))
	}

	fun loadPlaceDetails(id: Int){
		disposables.add(getPlaceDetailsExecution(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           placeDetailed.value = it
                           //Log.wtf(TAG, "$id place details = {$it}")
                       },
                       {
                           Log.wtf(TAG, "$it")
                       }))
	}




	fun getPlaceDetailed() = placeDetailed
	fun getPlacesList() = placesList





	private fun getPlacesExecution(category: String) = getPlacesUC.execute(category)
	private fun getPlaceDetailsExecution(id: Int) = getPlaceDetailsUC.execute(id)
}