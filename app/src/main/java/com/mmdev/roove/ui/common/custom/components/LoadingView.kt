/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.03.20 19:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.custom.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.mmdev.roove.R
import kotlin.math.min


/**
 * This is the documentation block about the class
 */

class LoadingView: View {

	private val mBackgroundOvalRect = RectF()
	private var mBackgroundOvalRadius = 0f
	private var mBackgroundColor: Int = ContextCompat.getColor(context, R.color.my_loading_background)
	private var mParentBoundLeft = 0f
	private var availableWidth = 0f


	private var childPositionX: Float = 0f
	private var mChildsOvalRadius = 0f

	private var mOval1Color: Int = ContextCompat.getColor(context, R.color.my_loading_shape1)
	private var mOval2Color: Int = ContextCompat.getColor(context, R.color.my_loading_shape2)
	private var mOval3Color: Int = ContextCompat.getColor(context, R.color.my_loading_shape3)

	private lateinit var animator: ValueAnimator

	private val mCircleBackgroundPaint = Paint(ANTI_ALIAS_FLAG).apply {
		style = Paint.Style.FILL
	}


	constructor(context: Context): super(context)

	@JvmOverloads
	constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0): super(context, attrs, defStyle)


	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
		val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
		val minSize = min(measuredWidth, measuredHeight)

		mBackgroundOvalRect.set(paddingLeft.toFloat(),
		                        paddingTop.toFloat(),
		                        (minSize - paddingRight).toFloat(),
		                        (minSize - paddingBottom).toFloat())
		mBackgroundOvalRadius = min(mBackgroundOvalRect.height() / 2.0f, mBackgroundOvalRect.width() / 2.0f)
		mChildsOvalRadius = mBackgroundOvalRadius / 2.5f
		setMeasuredDimension(minSize, minSize)
		childPositionX = mBackgroundOvalRect.centerX()

		startAnimation()
	}

	override fun onDraw(canvas: Canvas) {
		canvas.apply {
			//draw parent
			mCircleBackgroundPaint.color = mBackgroundColor
			drawCircle(mBackgroundOvalRect.centerX(),
			           mBackgroundOvalRect.centerY(),
			           mBackgroundOvalRadius,
			           mCircleBackgroundPaint)
			//blue circle
			mCircleBackgroundPaint.color = mOval3Color
			drawCircle(childPositionX*1.25f,
			           mBackgroundOvalRect.centerY(),
			           mChildsOvalRadius,
			           mCircleBackgroundPaint)
			//red circle
			mCircleBackgroundPaint.color = mOval2Color
			drawCircle(childPositionX/1.5f,
			           mBackgroundOvalRect.centerY(),
			           mChildsOvalRadius,
			           mCircleBackgroundPaint)
			//green circle
			mCircleBackgroundPaint.color = mOval1Color
			drawCircle(childPositionX,
			           mBackgroundOvalRect.centerY(),
			           mChildsOvalRadius,
			           mCircleBackgroundPaint)

		}

	}

	private fun startAnimation() {
		animator = ValueAnimator.ofFloat(300f,
		                                 (mBackgroundOvalRect.centerX() +
		                                  mBackgroundOvalRect.centerX()/2f)).apply {
			duration = 3000
			repeatCount = ValueAnimator.INFINITE
			repeatMode = ValueAnimator.RESTART

			addUpdateListener { valueAnimator ->
				childPositionX = valueAnimator.animatedValue as Float
				invalidate()
			}
		}
		animator.start()
	}

}