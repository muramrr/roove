/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 19:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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