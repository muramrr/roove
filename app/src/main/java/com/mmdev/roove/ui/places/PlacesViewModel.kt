/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:58
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
import com.mmdev.business.places.repository.PlacesRepository
import com.mmdev.business.places.usecase.GetPlaceDetailsUseCase
import com.mmdev.business.places.usecase.GetPlacesUseCase
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PlacesViewModel @Inject constructor(repo: PlacesRepository): BaseViewModel() {

	private val getPlacesUC = GetPlacesUseCase(repo)
	private val getPlaceDetailsUC = GetPlaceDetailsUseCase(repo)

	val placesList: MutableLiveData<List<PlaceItem>> = MutableLiveData()
	val placeDetailed: MutableLiveData<PlaceDetailedItem> = MutableLiveData()

	fun loadPlaces(category: String){
		disposables.add(getPlacesExecution(category)
            .retry(5)
            .observeOn(mainThread())
            .subscribe({
	                       placesList.value = it.results
	                       Log.wtf(TAG, "$category to display: ${it.results.size}")
                       },
                       {
	                       error.value = MyError(ErrorType.LOADING, it)
                       }))
	}

	fun loadPlaceDetails(id: Int){
		disposables.add(getPlaceDetailsExecution(id)
            .observeOn(mainThread())
            .subscribe({
                           placeDetailed.value = it
                           //Log.wtf(TAG, "$id place details = {$it}")
                       },
                       {
	                       error.value = MyError(ErrorType.LOADING, it)
                       }))
	}


	private fun getPlacesExecution(category: String) = getPlacesUC.execute(category)
	private fun getPlaceDetailsExecution(id: Int) = getPlaceDetailsUC.execute(id)
}