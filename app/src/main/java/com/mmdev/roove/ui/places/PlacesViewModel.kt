/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.04.20 17:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.places.BasePlaceInfo
import com.mmdev.business.places.PlaceDetailedItem
import com.mmdev.business.places.PlaceItem
import com.mmdev.business.places.repository.PlacesRepository
import com.mmdev.business.places.usecase.*
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PlacesViewModel @Inject constructor(repo: PlacesRepository): BaseViewModel() {

	private val addPlaceUC = AddPlaceToWantToGoListUseCase(repo)
	private val loadFirstPlacesUC = LoadFirstPlacesUseCase(repo)
	private val loadMorePlacesUC = LoadMorePlacesUseCase(repo)
	private val getPlaceDetailsUC = GetPlaceDetailsUseCase(repo)
	private val removePlaceUC = RemovePlaceFromWantToGoListUseCase(repo)

	val placesList: MutableLiveData<MutableList<PlaceItem>> = MutableLiveData(mutableListOf())
	val placeDetailed: MutableLiveData<PlaceDetailedItem> = MutableLiveData()
	val isAddedToProfile: MutableLiveData<Boolean> = MutableLiveData()
	val showTextHelper: MutableLiveData<Boolean> = MutableLiveData()

	fun addPlaceToProfile(basePlaceInfo: BasePlaceInfo){
		disposables.add(addPlaceExecution(basePlaceInfo)
            .observeOn(mainThread())
            .subscribe({
                           isAddedToProfile.value = true
                       },
                       {
                           error.value = MyError(ErrorType.SUBMITING, it)
                       }))
	}


	fun loadFirstPlaces(category: String){
		disposables.add(loadFirstPlacesExecution(category)
            .retry(3)
            .observeOn(mainThread())
            .subscribe({
	                       if (it.results.isNotEmpty()) {
		                       placesList.value = it.results.toMutableList()
		                       showTextHelper.value = false
	                       }
	                       else showTextHelper.value = true
                       },
                       {
	                       error.value = MyError(ErrorType.LOADING, it)
                       }))
	}

	fun loadMorePlaces(category: String){
		disposables.add(loadMorePlacesExecution(category)
            .retry(3)
            .observeOn(mainThread())
            .subscribe({
                           if (it.results.isNotEmpty()) {
	                           placesList.value!!.addAll(it.results)
	                           placesList.value = placesList.value
                           }
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

	fun removePlaceFromProfile(basePlaceInfo: BasePlaceInfo){
		disposables.add(removePlaceExecution(basePlaceInfo)
            .observeOn(mainThread())
            .subscribe({
	                       isAddedToProfile.value = false
                       },
                       {
                           error.value = MyError(ErrorType.DELETING, it)
                       }))
	}


	private fun addPlaceExecution(basePlaceInfo: BasePlaceInfo) = addPlaceUC.execute(basePlaceInfo)
	private fun loadFirstPlacesExecution(category: String) = loadFirstPlacesUC.execute(category)
	private fun loadMorePlacesExecution(category: String) = loadMorePlacesUC.execute(category)
	private fun getPlaceDetailsExecution(id: Int) = getPlaceDetailsUC.execute(id)
	private fun removePlaceExecution(basePlaceInfo: BasePlaceInfo) = removePlaceUC.execute(basePlaceInfo)
}