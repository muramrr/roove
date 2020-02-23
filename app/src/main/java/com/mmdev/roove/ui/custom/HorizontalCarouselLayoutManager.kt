/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.02.20 16:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import kotlin.math.abs


/**
 * This is the documentation block about the class
 */
class HorizontalCarouselLayoutManager: LinearLayoutManager {

	private val mShrinkAmount = 0.15f
	private val mShrinkDistance = 0.9f

	constructor(context: Context?): super(context)
	constructor(context: Context?, orientation: Int, reverseLayout: Boolean): super(context, orientation, reverseLayout)



	override fun scrollVerticallyBy(dy: Int, recycler: Recycler?, state: RecyclerView.State?): Int {
		return if (orientation == VERTICAL) {
			val scrolled = super.scrollVerticallyBy(dy, recycler, state)
			val midpoint = height / 2f
			val d0 = 0f
			val d1 = mShrinkDistance * midpoint
			val s0 = 1f
			val s1 = 1f - mShrinkAmount
			for (i in 0 until childCount) {
				val child = getChildAt(i)
				if (child != null) {
					val childMidpoint = (getDecoratedBottom(child) + getDecoratedTop(child)) / 2f
					val d = d1.coerceAtMost(abs(midpoint - childMidpoint))
					val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
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
			val midpoint = width / 2f
			val d0 = 0f
			val d1 = mShrinkDistance * midpoint
			val s0 = 1f
			val s1 = 1f - mShrinkAmount
			for (i in 0 until childCount) {

				val child = getChildAt(i)
				if (child != null) {
					val childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2f
					val d = d1.coerceAtMost(abs(midpoint - childMidpoint))
					val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
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
