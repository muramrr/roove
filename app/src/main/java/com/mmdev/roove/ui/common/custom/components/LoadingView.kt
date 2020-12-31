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

package com.mmdev.roove.ui.common.custom.components

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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
	private var mBackgroundColor: Int = DEFAULT_CONTAINER_BACKGROUND_COLOR

	private var mChildrenOvalRadius = 0f

	//this positions calculated only in onMeasure
	private var blueChildStartPosition = 0f
	private var greenChildStartPosition = 0f
	private var redChildStartPosition = 0f

	//this value respond to position circle during animations
	private var blueChildFloatingPosition = 0f
	private var greenChildFloatingPosition = 0f
	private var redChildFloatingPosition = 0f

	private val greenColor: Int = ContextCompat.getColor(context, R.color.my_loading_green_oval)
	private val blueColor: Int = ContextCompat.getColor(context, R.color.my_loading_blue_oval)
	private val redColor: Int = ContextCompat.getColor(context, R.color.my_loading_red_oval)

	private val animator: AnimatorSet = AnimatorSet()

	private val mCircleBackgroundPaint = Paint(ANTI_ALIAS_FLAG).apply {
		style = Paint.Style.FILL
	}

	companion object{
		private const val DEFAULT_CONTAINER_BACKGROUND_COLOR = Color.TRANSPARENT
	}

	constructor(context: Context): super(context)

	@JvmOverloads
	constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0): super(context, attrs, defStyle){
		val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyle, 0)
		mBackgroundColor = a.getColor(R.styleable.LoadingView_container_background_color,
		                              DEFAULT_CONTAINER_BACKGROUND_COLOR)
		a.recycle()
	}


	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
		val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
		val minSize = min(measuredWidth, measuredHeight)

		mBackgroundOvalRect.set(paddingLeft.toFloat(),
		                        paddingTop.toFloat(),
		                        (minSize - paddingRight).toFloat(),
		                        (minSize - paddingBottom).toFloat())
		mBackgroundOvalRadius = min(mBackgroundOvalRect.height() / 2.0f, mBackgroundOvalRect.width() / 2.0f)
		mChildrenOvalRadius = mBackgroundOvalRadius / 2.5f
		setMeasuredDimension(minSize, minSize)

		blueChildStartPosition = mBackgroundOvalRect.centerX()*1.3f
		redChildStartPosition = mBackgroundOvalRect.centerX()/1.4f
		greenChildStartPosition = mBackgroundOvalRect.centerX()

		blueChildFloatingPosition = blueChildStartPosition
		redChildFloatingPosition = redChildStartPosition
		greenChildFloatingPosition = greenChildStartPosition

		animator.playTogether(startAnimationBlue(),
		                      startAnimationRed(),
		                      startAnimationGreen())
		animator.start()
	}

	override fun onDraw(canvas: Canvas) {
		canvas.apply {
			//draw parent
			mCircleBackgroundPaint.color = mBackgroundColor
			drawCircle(mBackgroundOvalRect.centerX(),
			           mBackgroundOvalRect.centerY(),
			           mBackgroundOvalRadius,
			           mCircleBackgroundPaint)
			//green circle
			mCircleBackgroundPaint.color = greenColor
			drawCircle(greenChildFloatingPosition,
			           mBackgroundOvalRect.centerY(),
			           mChildrenOvalRadius,
			           mCircleBackgroundPaint)
			//blue circle
			mCircleBackgroundPaint.color = blueColor
			drawCircle(blueChildFloatingPosition,
			           mBackgroundOvalRect.centerY(),
			           mChildrenOvalRadius,
			           mCircleBackgroundPaint)
			//red circle
			mCircleBackgroundPaint.color = redColor
			drawCircle(redChildFloatingPosition,
			           mBackgroundOvalRect.centerY(),
			           mChildrenOvalRadius,
			           mCircleBackgroundPaint)

		}

	}

	private fun startAnimationBlue(): ValueAnimator {
		return ValueAnimator.ofFloat(blueChildStartPosition,
		                             mBackgroundOvalRect.centerX()*0.7f,
		                             blueChildStartPosition).apply {
			duration = 2000
			repeatCount = ValueAnimator.INFINITE
			repeatMode = ValueAnimator.RESTART

			addUpdateListener { valueAnimator ->
				blueChildFloatingPosition = valueAnimator.animatedValue as Float
				invalidate()
			}
		}
	}

	private fun startAnimationRed(): ValueAnimator {
		return ValueAnimator.ofFloat(redChildStartPosition,
		                             mBackgroundOvalRect.centerX()*1.4f,
		                             redChildStartPosition).apply {
			duration = 1800
			repeatCount = ValueAnimator.INFINITE
			repeatMode = ValueAnimator.RESTART

			addUpdateListener { valueAnimator ->
				redChildFloatingPosition = valueAnimator.animatedValue as Float
				invalidate()
			}
		}
	}

	private fun startAnimationGreen(): ValueAnimator {
		return ValueAnimator.ofFloat(greenChildStartPosition/2,
		                             mBackgroundOvalRect.centerX()*1.5f,
		                             greenChildStartPosition/2).apply {
			duration = 3500
			repeatCount = ValueAnimator.INFINITE
			repeatMode = ValueAnimator.RESTART

			addUpdateListener { valueAnimator ->
				greenChildFloatingPosition = valueAnimator.animatedValue as Float
				invalidate()
			}
		}
	}

}