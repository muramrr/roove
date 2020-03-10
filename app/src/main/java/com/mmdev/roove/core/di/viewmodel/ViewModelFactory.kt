/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.03.20 19:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Please keep in mind that the class itself is marked as @[Singleton],
 * there are several approaches here,
 * if you want to keep all [ViewModels] in the application throughout your work in the same factory,
 * mark it as a [Singleton],
 * make one module with all the [ViewModels]
 * and add the model binding keys into [ViewModelModule]
 */

@Singleton
@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>,
		@JvmSuppressWildcards Provider<ViewModel>>) :
		ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		val viewModelProvider = viewModels[modelClass]
		                        ?: throw IllegalArgumentException("model class $modelClass not found")
		try {
			return viewModelProvider.get() as T
		} catch (e: Exception) {
			throw RuntimeException(e)
		}
	}

}