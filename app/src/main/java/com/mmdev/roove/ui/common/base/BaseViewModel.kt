/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.roove.ui.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.roove.core.log.logDebug
import com.mmdev.roove.ui.common.errors.MyError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Generic class for ViewModels
 */

abstract class BaseViewModel: ViewModel() {

	internal val error = MutableLiveData<MyError>()
	protected val disposables = CompositeDisposable()
	protected val TAG = "mylogs_${javaClass.simpleName}"

	protected fun mainThread(): Scheduler = AndroidSchedulers.mainThread()

	override fun onCleared() {
		disposables.clear()
		logDebug(TAG, "${javaClass.simpleName} cleared.")
		super.onCleared()
	}
}