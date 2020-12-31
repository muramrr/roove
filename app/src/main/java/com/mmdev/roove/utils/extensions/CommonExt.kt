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

package com.mmdev.roove.utils.extensions

import android.content.Context
import android.widget.Toast
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

fun BaseViewModel.showErrorDialog(lifecycleOwner: LifecycleOwner, context: Context?) {
	this.error.observe(lifecycleOwner, { myError ->
		context?.let {
			MaterialAlertDialogBuilder(it)
				.setTitle(R.string.dialog_error_title)
				.setMessage(myError.getErrorMessage())
				.create()
				.show()
		}
	})
}

