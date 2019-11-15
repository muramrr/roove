package com.mmdev.roove.ui.chat.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mmdev.roove.R
import com.mmdev.roove.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity: AppCompatActivity() {

	private var isHide = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val binding: ActivityFullScreenImageBinding = DataBindingUtil
			.setContentView(this, R.layout.activity_full_screen_image)

		binding.photoUrl = intent.getStringExtra("urlPhotoClick")!!
		val imageView = findViewById<ImageView>(R.id.imageView)
		imageView.setOnClickListener { fullScreenCall() }
		// Set the content to appear under the system bars so that the
		// content doesn't resize when the system bars hide and show.
		imageView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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


//	override fun onBackPressed() {
//		super.onBackPressed()
//		Runtime.getRuntime().gc()
//		finish()
//	}

}
