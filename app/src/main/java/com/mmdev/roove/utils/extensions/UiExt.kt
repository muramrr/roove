/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

/**
 * Show the view (visibility = View.VISIBLE)
 */
fun View.visible(): View {
	if (visibility != View.VISIBLE) visibility = View.VISIBLE
	return this
}

fun View.visibleWithAnimation(delay: Int = 0): View {
	if (visibility != View.VISIBLE) {
		//check if previous state was GONE to prevent unexpected crash "No such view"
		if (visibility == View.GONE) visibility = View.INVISIBLE
		clearAnimation()
		alpha = 0.0f
		visibility = View.VISIBLE
		animate().alpha(1.0f).setDuration(delay.toLong()).setListener(null)
	}
	return this
}

/**
 * Hide the view (visibility = [View.INVISIBLE])
 */
fun View.invisible(): View {
	if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
	return this
}

fun View.invisibleWithAnimation(delay: Int = 0): View {
	if (visibility != View.INVISIBLE) {
		clearAnimation()
		animate().alpha(0.0f).setDuration(delay.toLong())
			.setListener(object: AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					visibility = View.INVISIBLE
				}
			})
		
	}
	return this
}

/**
 * Remove the view (visibility = View.GONE)
 */
fun View.gone(): View {
	if (visibility != View.GONE) visibility = View.GONE
	return this
}

fun View.goneWithAnimation(delay: Int = 0): View {
	if (visibility != View.GONE) {
		clearAnimation()
		animate().alpha(0.0f).setDuration(delay.toLong())
			.setListener(object: AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					clearAnimation()
					visibility = View.GONE
				}
			})
	}
	return this
}
