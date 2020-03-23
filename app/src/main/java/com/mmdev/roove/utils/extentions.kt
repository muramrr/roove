/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.03.20 18:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import android.content.Context
import android.view.View
import android.view.WindowInsets
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.roove.R
import com.mmdev.roove.ui.common.base.BaseViewModel


fun Context.showToastText(text: String) =
	Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
	observe(lifecycleOwner, object : Observer<T> {
		override fun onChanged(t: T?) {
			observer.onChanged(t)
			removeObserver(this)
		}
	})
}

fun Context.dp2px(dpValue: Float): Int = (dpValue * this.resources.displayMetrics.density + 0.5f).toInt()
fun Context.px2Dp(pxValue: Float): Int = (pxValue / this.resources.displayMetrics.density + 0.5f).toInt()


fun View.addSystemTopPadding(targetView: View = this) =
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(top = initialPadding.top + insets.systemWindowInsetTop)
	}


fun View.addSystemBottomPadding(targetView: View = this) =
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(bottom = initialPadding.bottom + insets.systemWindowInsetBottom)
	}

fun View.addSystemRightPadding(targetView: View = this) =
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(right = initialPadding.right + insets.systemWindowInsetRight)
	}


fun View.addSystemLeftPadding(targetView: View = this) =
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(left = initialPadding.left + insets.systemWindowInsetLeft)
	}


fun Context.ErrorMaterialDialogBuilder(errorText: String) =
	MaterialAlertDialogBuilder(this)
		.setTitle(getString(R.string.dialog_error_title))
		.setMessage(errorText)
		.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

fun BaseViewModel.showErrorDialog(lifecycleOwner: LifecycleOwner, context: Context?) {
	this.error.observe(lifecycleOwner, Observer {
		context?.ErrorMaterialDialogBuilder(it.getErrorMessage())?.show()
	})
}


fun View.doOnApplyWindowInsets(f: (View, WindowInsets, InitialPadding) -> Unit) {
	// Create a snapshot of the view's padding state
	val initialPadding = recordInitialPaddingForView(this)
	// Set an actual OnApplyWindowInsetsListener which proxies to the given
	// lambda, also passing in the original padding state
	setOnApplyWindowInsetsListener { v, insets ->
		f(v, insets, initialPadding)
		// Always return the insets, so that children can also use them
		insets
	}
	// request some insets
	requestApplyInsetsWhenAttached()
}

data class InitialPadding(val left: Int, val top: Int,
                          val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) =
	InitialPadding(view.paddingLeft, view.paddingTop,
	               view.paddingRight, view.paddingBottom)

private fun View.requestApplyInsetsWhenAttached() {
	if (isAttachedToWindow) {
		// We're already attached, just request as normal
		requestApplyInsets()
	} else {
		// We're not attached to the hierarchy, add a listener to
		// request when we are
		addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
			override fun onViewAttachedToWindow(v: View) {
				v.removeOnAttachStateChangeListener(this)
				v.requestApplyInsets()
			}

			override fun onViewDetachedFromWindow(v: View) = Unit
		})
	}
}