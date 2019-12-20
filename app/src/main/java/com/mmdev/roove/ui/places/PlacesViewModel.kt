/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 19.12.19 21:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.places

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.events.EventItem
import com.mmdev.business.events.usecase.GetEventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PlacesViewModel @Inject constructor(private val getEventsUC: GetEventsUseCase):
		ViewModel() {


	private val placesList: MutableLiveData<List<EventItem>> = MutableLiveData()

	private val disposables = CompositeDisposable()


	fun loadPlaces(){
		disposables.add(getEventsExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       placesList.value = it.results
                       },
                       {
                           Log.wtf("mylogs", "$it")
                       }))
	}


	fun getEventsList() = placesList



	private fun getEventsExecution() = getEventsUC.execute()


	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}
}