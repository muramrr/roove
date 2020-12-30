/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.12.20 21:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.roove.ui.common.errors.MyError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable


/**
 * generic class for viewmodels
 */

abstract class BaseViewModel: ViewModel() {

	internal val error: MutableLiveData<MyError> = MutableLiveData()
	protected val disposables = CompositeDisposable()
	protected val TAG = "mylogs_" + javaClass.simpleName

	protected fun mainThread(): Scheduler = AndroidSchedulers.mainThread()

	override fun onCleared() {
		disposables.clear()
		Log.wtf(TAG, "${javaClass.simpleName} on cleared called")
		super.onCleared()
	}
}