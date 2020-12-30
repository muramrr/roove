/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.12.20 21:52
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.mmdev.roove.BR
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.utils.showErrorDialog

/**
 * This is the documentation block about the class
 */

abstract class BaseFragment<T: ViewModel, Binding: ViewDataBinding> (
	val isViewModelActivityHosted: Boolean = false,
	@LayoutRes private val layoutId: Int = 0
) : Fragment() {
	
	private var _binding: Binding? = null
	
	protected val binding: Binding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)

	protected lateinit var navController: NavController
	protected val TAG = "mylogs_${javaClass.simpleName}"
	protected val factory = injector.factory()

	protected val sharedViewModel: SharedViewModel
		get() = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")


	protected lateinit var mViewModel: T

	private lateinit var callback: OnBackPressedCallback

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		navController = findNavController()
		setBackButtonDispatcher()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return DataBindingUtil.inflate<Binding>(inflater, layoutId, container, false)
			.apply {
				lifecycleOwner = viewLifecycleOwner
				setVariable(BR.viewModel, mViewModel)
				_binding = this
			}.root
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		(mViewModel as BaseViewModel).showErrorDialog(this, context)
	}

	protected inline fun <reified T : ViewModel> getViewModel(): T =
		if (isViewModelActivityHosted) {
			activity?.run {
				ViewModelProvider(this, factory)[T::class.java]
			} ?: throw Exception("Invalid Activity")
		}
		else ViewModelProvider(this, factory)[T::class.java]

	//get actual class from parameterized <T>
	//CAUTION: REFLECTION USED
	//use at own risk
//	private fun getTClass(): Class<T> =
//		(javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>


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



}