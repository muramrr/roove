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

package com.mmdev.roove.ui.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.mmdev.roove.BR
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.utils.extensions.showErrorDialog

/**
 * Generic Fragment class
 */

abstract class BaseFragment<T: ViewModel, Binding: ViewDataBinding>(
	@LayoutRes private val layoutId: Int
) : Fragment() {
	
	private var _binding: Binding? = null
	
	protected val binding: Binding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)

	protected lateinit var navController: NavController
	protected val TAG = "mylogs_${javaClass.simpleName}"

	protected val sharedViewModel: SharedViewModel by activityViewModels()


	protected abstract val mViewModel: T?

	private lateinit var callback: OnBackPressedCallback

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		navController = findNavController()
		setBackButtonDispatcher()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = DataBindingUtil.inflate<Binding>(inflater, layoutId, container, false).apply {
		lifecycleOwner = viewLifecycleOwner
		setVariable(BR.viewModel, mViewModel)
		_binding = this
	}.root
	

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		(mViewModel as BaseViewModel?)?.showErrorDialog(this, context)
	}
	
	/**
	 * Adding BackButtonDispatcher callback to activity
	 */
	private fun setBackButtonDispatcher() {
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				onBackPressed()
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	/**
	 * Override this method into your fragment to handle backButton
	 */
	open fun onBackPressed() {}
	
	override fun onDestroy() {
		_binding = null
		super.onDestroy()
	}
	
}