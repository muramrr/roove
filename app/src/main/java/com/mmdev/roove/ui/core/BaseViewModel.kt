/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 18:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * This is the documentation block about the class
 */

abstract class BaseViewModel: ViewModel() {

	internal val disposables = CompositeDisposable()
	internal val TAG = "mylogs_" + javaClass.simpleName

	override fun onCleared() {
		disposables.clear()
		Log.wtf(TAG, "on cleared called")
		super.onCleared()
	}
}