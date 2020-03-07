/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 19:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.custom


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R


/*
* SimpleCallback’s constructor is using two parameters — which directions we want to support,
* in our case just swipe from right to left.
* In onChildDraw method we need to manually calculate and draw background with icon.
*/


abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

	private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_24dp)!!
	private val intrinsicWidth = deleteIcon.intrinsicWidth
	private val intrinsicHeight = deleteIcon.intrinsicHeight
	private val background = ColorDrawable()
	private val backgroundColor = ContextCompat.getColor(context, R.color.material_red_a700)
	private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


	// We don't want support moving items up/down @return false
	override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
	                    target: RecyclerView.ViewHolder) =  false


	override fun onChildDraw(c: Canvas,
	                         recyclerView: RecyclerView,
	                         viewHolder: RecyclerView.ViewHolder,
	                         dX: Float, dY: Float,
	                         actionState: Int,
	                         isCurrentlyActive: Boolean) {

		val itemView = viewHolder.itemView
		val itemHeight = itemView.height
		val isCanceled = dX == 0f && !isCurrentlyActive

		if (isCanceled) {
			clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
			super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
			return
		}

		// Draw the red delete background
		background.color = backgroundColor
		background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
		background.draw(c)

		// Calculate position of delete icon
		val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
		val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
		val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
		val deleteIconRight = itemView.right - deleteIconMargin
		val deleteIconBottom = deleteIconTop + intrinsicHeight

		// Draw the delete icon
		deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
		deleteIcon.draw(c)

		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
	}

	// We’ve set the Swipe threshold to 0.7.
	// That means if the row is swiped less than 70%, the onSwipe method won’t be triggered.
	override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
		return 0.7f
	}

	private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
		c.drawRect(left, top, right, bottom, clearPaint)
	}
}