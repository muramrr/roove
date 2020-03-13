/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.03.20 19:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.custom.components


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.mmdev.roove.R
import kotlin.math.min
import kotlin.math.pow

/**
 * This is the documentation block about the class
 */


class CircleImageView: ImageView {

	private val mDrawableRect = RectF()
	private val mBorderRect = RectF()
	private val mShaderMatrix = Matrix()
	private val mBitmapPaint: Paint = Paint()

	private val mCircleBackgroundPaint = Paint()
	private var mCircleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR
	private var mImageAlpha = DEFAULT_IMAGE_ALPHA
	private var mBitmap: Bitmap? = null
	private var mBitmapShader: BitmapShader? = null
	private var mBitmapWidth = 0
	private var mBitmapHeight = 0
	private var mDrawableRadius = 0f
	private var mBorderRadius = 0f
	private var mColorFilter: ColorFilter? = null
	private var mReady = false
	private var mSetupPending = false
	private var mBorderOverlay = false
	private var mDisableCircularTransformation = false


	companion object {
		private val SCALE_TYPE = ScaleType.CENTER_CROP
		private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
		private const val COLORDRAWABLE_DIMENSION = 2
		private const val DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT
		private const val DEFAULT_IMAGE_ALPHA = 255
		private const val DEFAULT_BORDER_OVERLAY = false
	}

	constructor(context: Context): super(context) { init() }

	@JvmOverloads
	constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0): super(context, attrs, defStyle) {
		val a =
			context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0)

		mBorderOverlay =
			a.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY)
		mCircleBackgroundColor = a.getColor(R.styleable.CircleImageView_civ_circle_background_color,
		                                    DEFAULT_CIRCLE_BACKGROUND_COLOR)
		a.recycle()
		init()
	}

	private fun init() {
		super.setScaleType(SCALE_TYPE)
		mReady = true
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			outlineProvider = OutlineProvider()
		}
		if (mSetupPending) {
			setup()
			mSetupPending = false
		}
	}

	override fun getScaleType(): ScaleType = SCALE_TYPE

	override fun setScaleType(scaleType: ScaleType) {
		require(scaleType == SCALE_TYPE) {
			String.format("ScaleType %s not supported.", scaleType)
		}
	}

	override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
		require(!adjustViewBounds) { "adjustViewBounds not supported." }
	}

	override fun onDraw(canvas: Canvas) {
		if (mDisableCircularTransformation) {
			super.onDraw(canvas)
			return
		}
		if (mBitmap == null) {
			return
		}
		if (mCircleBackgroundColor != Color.TRANSPARENT) {
			canvas.drawCircle(mDrawableRect.centerX(),
			                  mDrawableRect.centerY(),
			                  mDrawableRadius,
			                  mCircleBackgroundPaint)
		}
		canvas.drawCircle(mDrawableRect.centerX(),
		                  mDrawableRect.centerY(),
		                  mDrawableRadius,
		                  mBitmapPaint)

	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		setup()
	}

	override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
		super.setPadding(left, top, right, bottom)
		setup()
	}

	override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
		super.setPaddingRelative(start, top, end, bottom)
		setup()
	}


	fun getCircleBackgroundColor(): Int = mCircleBackgroundColor

	fun setCircleBackgroundColor(@ColorInt circleBackgroundColor: Int) {
		if (circleBackgroundColor == mCircleBackgroundColor) {
			return
		}
		mCircleBackgroundColor = circleBackgroundColor
		mCircleBackgroundPaint.color = circleBackgroundColor
		invalidate()
	}

	fun setCircleBackgroundColorResource(@ColorRes circleBackgroundRes: Int) {
		setCircleBackgroundColor(ContextCompat.getColor(context, circleBackgroundRes))
	}


	fun isBorderOverlay(): Boolean = mBorderOverlay

	fun setBorderOverlay(borderOverlay: Boolean) {
		if (borderOverlay == mBorderOverlay) {
			return
		}
		mBorderOverlay = borderOverlay
		setup()
	}

	fun isDisableCircularTransformation(): Boolean = mDisableCircularTransformation

	fun setDisableCircularTransformation(disableCircularTransformation: Boolean) {
		if (mDisableCircularTransformation == disableCircularTransformation) {
			return
		}
		mDisableCircularTransformation = disableCircularTransformation
		initializeBitmap()
	}

	override fun setImageBitmap(bm: Bitmap?) {
		super.setImageBitmap(bm)
		initializeBitmap()
	}

	override fun setImageDrawable(drawable: Drawable?) {
		super.setImageDrawable(drawable)
		initializeBitmap()
	}

	override fun setImageResource(@DrawableRes resId: Int) {
		super.setImageResource(resId)
		initializeBitmap()
	}

	override fun setImageURI(uri: Uri?) {
		super.setImageURI(uri)
		initializeBitmap()
	}

	override fun setImageAlpha(alpha: Int) {
		val alpha1 = alpha and 0xFF
		if (alpha1 == mImageAlpha) {
			return
		}
		mImageAlpha = alpha1
		mBitmapPaint.alpha = mImageAlpha
		invalidate()
	}

	override fun getImageAlpha(): Int = mImageAlpha

	override fun setColorFilter(cf: ColorFilter) {
		if (cf === mColorFilter) {
			return
		}
		mColorFilter = cf
		// This might be called from setColorFilter during ImageView construction
		// before member initialization has finished on API level <= 19.
		mBitmapPaint.colorFilter = mColorFilter
		invalidate()
	}

	override fun getColorFilter(): ColorFilter? = mColorFilter

	private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
		if (drawable == null) {
			return null
		}
		return if (drawable is BitmapDrawable) {
			drawable.bitmap
		}
		else try {
			val bitmap: Bitmap = if (drawable is ColorDrawable) {
				Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
			}
			else {
				Bitmap.createBitmap(drawable.intrinsicWidth,
				                    drawable.intrinsicHeight,
				                    BITMAP_CONFIG)
			}
			val canvas = Canvas(bitmap)
			drawable.setBounds(0, 0, canvas.width, canvas.height)
			drawable.draw(canvas)
			bitmap
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}

	private fun initializeBitmap() {
		mBitmap = if (mDisableCircularTransformation) { null }
		else { getBitmapFromDrawable(drawable) }
		setup()
	}

	private fun setup() {
		if (!mReady) {
			mSetupPending = true
			return
		}
		if (width == 0 && height == 0) { return }

		if (mBitmap == null) {
			invalidate()
			return
		}
		mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
		mBitmapPaint.isAntiAlias = true
		mBitmapPaint.isDither = true
		mBitmapPaint.isFilterBitmap = true
		mBitmapPaint.shader = mBitmapShader

		mCircleBackgroundPaint.style = Paint.Style.FILL
		mCircleBackgroundPaint.isAntiAlias = true
		mCircleBackgroundPaint.color = mCircleBackgroundColor
		mBitmapHeight = mBitmap!!.height
		mBitmapWidth = mBitmap!!.width
		mBorderRect.set(calculateBounds())

		mDrawableRect.set(mBorderRect)

		mDrawableRadius = min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f)
		mBitmapPaint.alpha = mImageAlpha
		mBitmapPaint.colorFilter = mColorFilter
		updateShaderMatrix()
		invalidate()
	}

	private fun calculateBounds(): RectF {
		val availableWidth = width - paddingLeft - paddingRight
		val availableHeight = height - paddingTop - paddingBottom
		val sideLength = min(availableWidth, availableHeight)
		val left = paddingLeft + (availableWidth - sideLength) / 2f
		val top = paddingTop + (availableHeight - sideLength) / 2f
		return RectF(left, top, left + sideLength, top + sideLength)
	}

	private fun updateShaderMatrix() {
		val scale: Float
		var dx = 0f
		var dy = 0f
		mShaderMatrix.set(null)
		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
			scale = mDrawableRect.height() / mBitmapHeight.toFloat()
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
		}
		else {
			scale = mDrawableRect.width() / mBitmapWidth.toFloat()
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
		}
		mShaderMatrix.setScale(scale, scale)
		mShaderMatrix.postTranslate((dx + 0.5f).toInt() + mDrawableRect.left,
		                            (dy + 0.5f).toInt() + mDrawableRect.top)
		mBitmapShader!!.setLocalMatrix(mShaderMatrix)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		return if (mDisableCircularTransformation) {
			super.onTouchEvent(event)
		}
		else inTouchableArea(event.x, event.y) && super.onTouchEvent(event)
	}

	private fun inTouchableArea(x: Float, y: Float): Boolean {
		return if (mBorderRect.isEmpty) { true }
		else (x - mBorderRect.centerX().toDouble()).pow(2.0) +
		     (y - mBorderRect.centerY().toDouble()).pow(2.0) <= mBorderRadius.toDouble().pow(2.0)
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private inner class OutlineProvider: ViewOutlineProvider() {
		override fun getOutline(view: View, outline: Outline) {
			if (mDisableCircularTransformation) {
				BACKGROUND.getOutline(view, outline)
			}
			else {
				val bounds = Rect()
				mBorderRect.roundOut(bounds)
				outline.setRoundRect(bounds, bounds.width() / 2.0f)
			}
		}
	}


}