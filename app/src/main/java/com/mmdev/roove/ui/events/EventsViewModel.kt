/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 17:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.events

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.events.entity.EventItem
import com.mmdev.business.events.usecase.GetEventsUseCase
import com.mmdev.roove.ui.core.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class EventsViewModel @Inject constructor(private val getEventsUC: GetEventsUseCase):
		BaseViewModel() {

	private val eventsList: MutableLiveData<List<EventItem>> = MutableLiveData()


	fun loadEvents(){
		disposables.add(getEventsExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       eventsList.value = it.results
                       },
                       {
                           Log.wtf(TAG, "$it")
                       }))
	}


	fun getEventsList() = eventsList



	private fun getEventsExecution() = getEventsUC.execute()
}