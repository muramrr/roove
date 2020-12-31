/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
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
		val viewModelProvider =
			viewModels[modelClass] ?: throw IllegalArgumentException("model class $modelClass not found")
		return viewModelProvider.get() as T
	}

}