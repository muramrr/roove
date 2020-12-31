/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import kotlin.math.abs

/**
 * Set carousel appearance of horizontal linear layout manager
 */

class HorizontalCarouselLayoutManager @JvmOverloads constructor(
	context: Context,
	reverseLayout: Boolean,
	orientation: Int = RecyclerView.HORIZONTAL,
): LinearLayoutManager(context, orientation, reverseLayout) {
	
	private val mShrinkAmount = 0.25f
	private val mShrinkDistance = 0.9f
	
	override fun scrollVerticallyBy(dy: Int, recycler: Recycler?, state: RecyclerView.State?): Int {
		return if (orientation == VERTICAL) {
			val scrolled = super.scrollVerticallyBy(dy, recycler, state)
			val d0 = 0f
			val s0 = 1f
			val s1 = 1f - mShrinkAmount
			for (i in 0 until childCount) {
				val child = getChildAt(i)
				if (child != null) {
					val midpoint = height / 2f
					val d1 = mShrinkDistance * midpoint
					val childMidpoint = (getDecoratedBottom(child) + getDecoratedTop(child)) / 2f
					val d = d1.coerceAtMost(abs(midpoint - childMidpoint))
					var scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
					if (scale.isNaN()) scale = 0f
					
					child.scaleX = scale
					child.scaleY = scale
				}
			}
			scrolled
		}
		else { 0 }
	}
	
	override fun scrollHorizontallyBy(dx: Int, recycler: Recycler?, state: RecyclerView.State?): Int {
		return if (orientation == HORIZONTAL) {
			val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
			val d0 = 0f
			val s0 = 1f
			val s1 = 1f - mShrinkAmount
			for (i in 0 until childCount) {
				
				val child = getChildAt(i)
				if (child != null) {
					val midpoint = width / 2f
					val d1 = mShrinkDistance * midpoint
					val childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2f
					val d = d1.coerceAtMost(abs(midpoint - childMidpoint))
					var scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
					if (scale.isNaN()) scale = 0f
					
					child.scaleX = scale
					child.scaleY = scale
				}
			}
			scrolled
		}
		else { 0 }
	}
	
	//apply effect immediately
	override fun onLayoutChildren(recycler: Recycler?, state: RecyclerView.State?) {
		super.onLayoutChildren(recycler, state)
		scrollHorizontallyBy(0, recycler, state)
	}
}
