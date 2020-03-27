/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.core.schedulers

import io.reactivex.rxjava3.core.Scheduler

/**
 * This is the documentation block about the class
 */

interface ISchedulers {

	fun computation(): Scheduler
	fun trampoline(): Scheduler
	fun newThread(): Scheduler
	fun io(): Scheduler

}
