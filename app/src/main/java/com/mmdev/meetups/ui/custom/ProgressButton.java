package com.mmdev.meetups.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

import com.mmdev.meetups.R;

/**
 * Created by lumingmin on 6/3/16.
 */
public class ProgressButton extends View {


	private Paint paintRectF;
	private Paint paintText;

	private Paint paintPro;

	private int mPadding = 0;

	private float mSpac = 0;

	private ProgressButtonAnim mProgressButtonAnim;
	private ScaleAnimation mProgressScaleAnim;
	private RotateAnimation mProgressRotateAnim;
	private RectF mRectF = new RectF();
	private RectF mRectFPro = new RectF();

	private int progerssButtonDuration = 200;
	public void setProgressButtonAnim(int duration)
	{ this.progerssButtonDuration = duration;}

	private int scaleAnimationDuration = 300;
	public void setScaleAnimationDuration (int duration)
	{ this.scaleAnimationDuration = duration;}

	private int bgColor;
	public void setBgColor(int color)
	{ this.bgColor = color; }

	private int textColor;
	public void setTextColor(int color)
	{ this.textColor = color; }

	private int proColor;
	public void setProColor(int color)
	{ this.proColor = color; }

	private String text;
	public void setButtonText(String s) {
		this.text = s;
		invalidate();
	}

	private boolean mStop = false;

	public ProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.ProgressButton,
				0, 0);
		try {
			bgColor = a.getColor(R.styleable.ProgressButton_backgroundColor, Color.BLUE);
			proColor = a.getColor(R.styleable.ProgressButton_progressColor, Color.WHITE);
			text = a.getString(R.styleable.ProgressButton_text);
			textColor = a.getColor(R.styleable.ProgressButton_textColor, Color.WHITE);

		} finally {
			a.recycle();
		}
		initPaint();
	}

	private void initPaint() {
		int mStrokeWidth = dip2px(2);
		mPadding = dip2px(2);
		mProgressButtonAnim = new ProgressButtonAnim();
		mProgressRotateAnim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mProgressRotateAnim.setRepeatCount(-1);
		mProgressRotateAnim.setInterpolator(new LinearInterpolator());
		mProgressRotateAnim.setFillAfter(true);
		paintRectF = new Paint();
		paintRectF.setAntiAlias(true);
		paintRectF.setStyle(Paint.Style.FILL);
		paintRectF.setStrokeWidth(mStrokeWidth);

		paintText = new Paint();
		paintText.setAntiAlias(true);
		paintText.setStyle(Paint.Style.FILL);
		paintText.setTextSize(dip2px(15));
		paintPro = new Paint();
		paintPro.setAntiAlias(true);
		paintPro.setStyle(Paint.Style.STROKE);
		paintPro.setStrokeWidth((float) mStrokeWidth / 2);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paintText.setColor(textColor);
		paintRectF.setColor(bgColor);
		paintPro.setColor(proColor);


		mRectF.left = mPadding + mSpac;
		mRectF.top = mPadding;
		mRectF.right = getMeasuredWidth() - mPadding - mSpac;
		mRectF.bottom = getMeasuredHeight() - mPadding;
		float mRadius = (float) (getMeasuredHeight() - 2 * mPadding) / 2;

		canvas.drawRoundRect(mRectF, mRadius, mRadius, paintRectF);

		if (mRectF.width() == mRectF.height() && !mStop) {
			setClickable(true);

			mRectFPro.left = getMeasuredWidth() / 2.0f - mRectF.width() / 4;
			mRectFPro.top = getMeasuredHeight() / 2.0f - mRectF.width() / 4;
			mRectFPro.right = getMeasuredWidth() / 2.0f + mRectF.width() / 4;
			mRectFPro.bottom = getMeasuredHeight() / 2.0f + mRectF.width() / 4;
			float startAngle = 0f;
			canvas.drawArc(mRectFPro, startAngle, 100, false, paintPro);
		}

		if (mSpac < (getMeasuredWidth() - getMeasuredHeight()) / 2.0f)
			canvas.drawText(text,
					getMeasuredWidth() / 2.0f - getFontlength(paintText, text) / 2.0f,
					getMeasuredHeight() / 2.0f + getFontHeight(paintText, text) / 3.0f,
					paintText);

	}

	public void startAnim() {
		mStop = false;
		setClickable(false);
		if (mProgressButtonAnim != null) {
			clearAnimation();
			mProgressButtonAnim.setDuration(progerssButtonDuration);
		}
		startAnimation(mProgressButtonAnim);
	}

	public void startProAnim() {
		if (mProgressRotateAnim != null) {
			clearAnimation();
			int rotateAnimationDuration = 400;
			mProgressRotateAnim.setDuration(rotateAnimationDuration);
		}
		startAnimation(mProgressRotateAnim);
	}

	public void stopAnim(final OnStopAnim mOnStopAnim) {
		clearAnimation();
		mStop = true;
		invalidate();
		if (mProgressScaleAnim != null)
			clearAnimation();
		else {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getDisplay().getMetrics(displayMetrics);
			int width = displayMetrics.widthPixels;

			mProgressScaleAnim = new ScaleAnimation(1.0f, (float)width / getMeasuredHeight() * 3.5f,
					1.0f, (float)width / getMeasuredHeight() * 3.5f,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		}

		mProgressScaleAnim.setDuration(scaleAnimationDuration);
		startAnimation(mProgressScaleAnim);
		mProgressScaleAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) { }

			@Override
			public void onAnimationEnd(Animation animation) {
				mOnStopAnim.Stop();
				clearAnimation();
				mSpac = 0;
				invalidate();
			}

			@Override
			public void onAnimationRepeat(Animation animation) { }
		});
	}


	private class ProgressButtonAnim
			extends Animation {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			mSpac = (getMeasuredWidth() - getMeasuredHeight()) / 2.0f * interpolatedTime;
			invalidate();
			if (interpolatedTime == 1.0f) startProAnim();
		}
	}

	public int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public float getFontlength(Paint paint, String str) {
		Rect rect = new Rect();
		paint.getTextBounds(str, 0, str.length(), rect);
		return rect.width();
	}

	public float getFontHeight(Paint paint, String str) {
		Rect rect = new Rect();
		paint.getTextBounds(str, 0, str.length(), rect);
		return rect.height();

	}

	public interface OnStopAnim {
		void Stop();
	}


}
