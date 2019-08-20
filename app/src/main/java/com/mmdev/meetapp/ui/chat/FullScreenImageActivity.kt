package com.mmdev.meetapp.ui.chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.mmdev.meetapp.R
import com.mmdev.meetapp.utils.GlideApp

class FullScreenImageActivity: AppCompatActivity() {

	private var mImageView: PhotoView? = null
	private var isHide = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_full_screen_image)
		bindViews()
	}

	private fun bindViews() {

		mImageView = findViewById(R.id.imageView)
		mImageView!!.setOnClickListener { fullScreenCall() }
		// Set the content to appear under the system bars so that the
		// content doesn't resize when the system bars hide and show.
		mImageView!!.systemUiVisibility =
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	}

	private fun setValues() {
		val urlPhotoClick: String? = intent.getStringExtra("urlPhotoClick")
		Log.i("TAG", "image received$urlPhotoClick")
		GlideApp.with(this).load(urlPhotoClick).into(mImageView!!)
	}

	//hide bottom navigation to see fullscreen image
	private fun fullScreenCall() {
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
			// lower api
			val v = this.window.decorView
			if (v.systemUiVisibility == View.VISIBLE) v.systemUiVisibility = View.GONE
			else v.systemUiVisibility = View.VISIBLE
		}
		else if (Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			val decorView = window.decorView
			if (!isHide) {
				decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
				isHide = true
			}
			else {
				decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
				isHide = false
			}
		}
	}

	override fun onResume() {
		super.onResume()
		setValues()
	}

	override fun onBackPressed() {
		super.onBackPressed()
		Runtime.getRuntime().gc()
		finish()
	}
}
