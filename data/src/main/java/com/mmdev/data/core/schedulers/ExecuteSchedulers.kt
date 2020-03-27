/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.core.schedulers


import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * app related schedulers class that used in repositoriesImpl to avoid direct dependencies on
 * RxJava library values and imports
 */

object ExecuteSchedulers: ISchedulers {

	override fun computation(): Scheduler = Schedulers.computation()
	override fun trampoline(): Scheduler = Schedulers.trampoline()
	override fun newThread(): Scheduler = Schedulers.newThread()
	override fun io(): Scheduler = Schedulers.io()

}
