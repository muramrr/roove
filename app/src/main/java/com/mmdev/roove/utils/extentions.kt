/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.02.20 14:55
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import android.graphics.Rect
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun AppCompatActivity.showToastText(text: String) =
	Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
	observe(lifecycleOwner, object : Observer<T> {
		override fun onChanged(t: T?) {
			observer.onChanged(t)
			removeObserver(this)
		}
	})
}


fun View.addSystemTopPadding(targetView: View = this, isConsumed: Boolean = false) {
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(top = initialPadding.top + insets.systemWindowInsetTop)

		if (isConsumed) {
			insets.replaceSystemWindowInsets(Rect(insets.systemWindowInsetLeft,
				                                0,
				                                insets.systemWindowInsetRight,
				                                insets.systemWindowInsetBottom))!!
		} else {
			insets
		}
	}
}

fun View.addSystemBottomPadding(targetView: View = this, isConsumed: Boolean = false) {
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(bottom = initialPadding.bottom + insets.systemWindowInsetBottom)

		if (isConsumed) {
			insets.replaceSystemWindowInsets(Rect(insets.systemWindowInsetLeft,
				                                insets.systemWindowInsetTop,
				                                insets.systemWindowInsetRight,
				                                0))!!
		} else {
			insets
		}
	}
}

fun View.addSystemRightPadding(targetView: View = this, isConsumed: Boolean = false) {
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(right = initialPadding.right + insets.systemWindowInsetRight)

		if (isConsumed) {
			insets.replaceSystemWindowInsets(Rect(insets.systemWindowInsetLeft,
				                                insets.systemWindowInsetTop,
				                                0,
				                                insets.systemWindowInsetBottom))!!
		} else {
			insets
		}
	}
}

fun View.addSystemLeftPadding(targetView: View = this, isConsumed: Boolean = false) {
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(left = initialPadding.left + insets.systemWindowInsetLeft)

		if (isConsumed) {
			insets.replaceSystemWindowInsets(Rect(0,
				                                insets.systemWindowInsetTop,
				                                insets.systemWindowInsetRight,
				                                insets.systemWindowInsetBottom))!!
		} else {
			insets
		}
	}
}


fun View.doOnApplyWindowInsets(block: (View, WindowInsetsCompat, Rect) -> WindowInsetsCompat) {
	val initialPadding = recordInitialPaddingForView(this)
	ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
		block(v, insets, initialPadding)

	}
	requestApplyInsetsWhenAttached()
}

private fun recordInitialPaddingForView(view: View) =
	Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

private fun View.requestApplyInsetsWhenAttached() {
	if (isAttachedToWindow) {
		ViewCompat.requestApplyInsets(this)
	}
	else {
		addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
			override fun onViewAttachedToWindow(v: View) {
				v.removeOnAttachStateChangeListener(this)
				ViewCompat.requestApplyInsets(v)
			}

			override fun onViewDetachedFromWindow(v: View) = Unit
		})
	}
}
