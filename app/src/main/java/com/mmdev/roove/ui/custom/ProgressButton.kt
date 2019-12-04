/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.*
import android.view.animation.Animation.AnimationListener
import com.mmdev.roove.R.styleable

class ProgressButton: View {
	private var paintRectF: Paint? = null
	private var paintText: Paint? = null
	private var paintPro: Paint? = null
	private var mPadding = 0
	private var mSpac = 0f
	private var mProgressButtonAnim: ProgressButtonAnim? = null
	private var mProgressScaleAnim: ScaleAnimation? = null
	private var mProgressRotateAnim: RotateAnimation? = null
	private val mRectF = RectF()
	private val mRectFPro = RectF()

	private var bgColor = 0
	fun setBgColor(color: Int) {
		bgColor = color
	}

	private var textColor = 0
	fun setTextColor(color: Int) {
		textColor = color
	}

	private var progressColor = 0
	fun setProgressColor(color: Int) {
		progressColor = color
	}

	private var buttonText: String? = null
	fun setButtonText(s: String?) {
		buttonText = s
		invalidate()
	}

	private var progressButtonDuration = 200
	fun setProgressButtonDuration(time: Int) {
		progressButtonDuration = time
	}

	private var progressAnimationSpeed = 400
	fun setProgressAnimationSpeed(time: Int) {
		progressAnimationSpeed = time
	}

	private var scaleAnimationDuration = 300
	fun setScaleAnimationDuration(time: Int) {
		scaleAnimationDuration = time
	}

	private var mStarted = false
	private var mStop = false

	constructor(context: Context?): super(context)

	constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
		val a: TypedArray =
			context.theme.obtainStyledAttributes(attrs, styleable.ProgressButton, 0, 0)
		try {
			bgColor = a.getColor(styleable.ProgressButton_backgroundColor, Color.BLUE)
			buttonText = a.getString(styleable.ProgressButton_text)
			progressColor = a.getColor(styleable.ProgressButton_progressColor, Color.WHITE)
			textColor = a.getColor(styleable.ProgressButton_textColor, Color.WHITE)
		} finally {
			a.recycle()
		}
		initPaint()
	}

	fun startAnim() {
		if (!mStarted) {
			mStarted = true
			mStop = false
			isClickable = false
			if (mProgressButtonAnim != null) {
				clearAnimation()
				mProgressButtonAnim!!.duration = progressButtonDuration.toLong()
			}
			startAnimation(mProgressButtonAnim)
		}
	}

	fun stopAnim(mOnStopAnim: OnStopAnim) {
		clearAnimation()
		mStop = true
		invalidate()
		if (mProgressScaleAnim != null) clearAnimation()
		else {
			val displayMetrics = DisplayMetrics()
			display.getMetrics(displayMetrics)
			val width = displayMetrics.widthPixels
			mProgressScaleAnim = ScaleAnimation(1.0f,
			                                    width.toFloat() / measuredHeight * 3.5f,
			                                    1.0f,
			                                    width.toFloat() / measuredHeight * 3.5f,
			                                    Animation.RELATIVE_TO_SELF,
			                                    0.5f,
			                                    Animation.RELATIVE_TO_SELF,
			                                    0.5f)
		}
		mProgressScaleAnim!!.duration = scaleAnimationDuration.toLong()
		startAnimation(mProgressScaleAnim)
		mProgressScaleAnim!!.setAnimationListener(object: AnimationListener {
			override fun onAnimationStart(animation: Animation?) {
			}

			override fun onAnimationEnd(animation: Animation?) {
				clearAnimation()
				mOnStopAnim.stop()
				mSpac = 0f
				invalidate()
			}

			override fun onAnimationRepeat(animation: Animation?) {
			}
		})
	}

	private fun initPaint() {
		val mStrokeWidth = dip2px(2f)
		mPadding = dip2px(2f)
		mProgressButtonAnim = ProgressButtonAnim()
		mProgressRotateAnim = RotateAnimation(0f,
		                                      360f,
		                                      Animation.RELATIVE_TO_SELF,
		                                      0.5f,
		                                      Animation.RELATIVE_TO_SELF,
		                                      0.5f)
		mProgressRotateAnim!!.repeatCount = -1
		mProgressRotateAnim!!.interpolator = LinearInterpolator()
		mProgressRotateAnim!!.fillAfter = true
		paintRectF = Paint()
		paintRectF!!.isAntiAlias = true
		paintRectF!!.style = Style.FILL
		paintRectF!!.strokeWidth = mStrokeWidth.toFloat()
		paintText = Paint()
		paintText!!.isAntiAlias = true
		paintText!!.style = Style.FILL
		paintText!!.textSize = dip2px(15f).toFloat()
		paintPro = Paint()
		paintPro!!.isAntiAlias = true
		paintPro!!.style = Style.STROKE
		paintPro!!.strokeWidth = mStrokeWidth.toFloat() / 2
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		paintText!!.color = textColor
		paintRectF!!.color = bgColor
		paintPro!!.color = progressColor
		mRectF.left = mPadding + mSpac
		mRectF.top = mPadding.toFloat()
		mRectF.right = measuredWidth - mPadding - mSpac
		mRectF.bottom = measuredHeight - mPadding.toFloat()
		val mRadius = (measuredHeight - 2 * mPadding).toFloat() / 2
		canvas.drawRoundRect(mRectF, mRadius, mRadius, paintRectF!!)
		if (mRectF.width() == mRectF.height() && !mStop) {
			isClickable = true
			mRectFPro.left = measuredWidth / 2.0f - mRectF.width() / 4
			mRectFPro.top = measuredHeight / 2.0f - mRectF.width() / 4
			mRectFPro.right = measuredWidth / 2.0f + mRectF.width() / 4
			mRectFPro.bottom = measuredHeight / 2.0f + mRectF.width() / 4
			val startAngle = 0f
			canvas.drawArc(mRectFPro, startAngle, 100f, false, paintPro!!)
		}
		if (mSpac < (measuredWidth - measuredHeight) / 2.0f) canvas.drawText(buttonText!!,
		                                                                     measuredWidth / 2.0f - getFontLength(
				                                                                     paintText,
				                                                                     buttonText) / 2.0f,
		                                                                     measuredHeight / 2.0f + getFontHeight(
				                                                                     paintText,
				                                                                     buttonText) / 3.0f,
		                                                                     paintText!!)
	}

	private fun progressAnim() {
		if (mProgressRotateAnim != null) {
			clearAnimation()
			mProgressRotateAnim!!.duration = progressAnimationSpeed.toLong()
		}
		startAnimation(mProgressRotateAnim)
	}

	private fun dip2px(dpValue: Float): Int {
		val scale = context.resources.displayMetrics.density
		return (dpValue * scale + 0.5f).toInt()
	}

	private fun getFontLength(paint: Paint?, str: String?): Float {
		val rect = Rect()
		paint!!.getTextBounds(str, 0, str!!.length, rect)
		return rect.width().toFloat()
	}

	private fun getFontHeight(paint: Paint?, str: String?): Float {
		val rect = Rect()
		paint!!.getTextBounds(str, 0, str!!.length, rect)
		return rect.height().toFloat()
	}

	private inner class ProgressButtonAnim: Animation() {
		override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
			super.applyTransformation(interpolatedTime, t)
			mSpac = (measuredWidth - measuredHeight) / 2.0f * interpolatedTime
			invalidate()
			if (interpolatedTime == 1.0f) progressAnim()
		}
	}

	interface OnStopAnim {
		fun stop()
	}
}