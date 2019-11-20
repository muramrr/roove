/*
 * Created by Andrii Kovalchuk on 20.11.19 21:38
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.11.19 21:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.events.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.events.usecase.GetEventsUseCase

/**
 * This is the documentation block about the class
 */

class EventsViewModel(private val getEventsUC: GetEventsUseCase): ViewModel() {

	fun getEvents() = getEventsUC.execute()

}