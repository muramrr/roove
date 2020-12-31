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
