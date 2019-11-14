package com.mmdev.roove.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.auth.view.AuthActivity
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModel
import com.mmdev.roove.ui.main.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class SplashActivity: AppCompatActivity(R.layout.activity_splash) {

	companion object{
		private const val TAG = "mylogs"
	}

	private val disposables = CompositeDisposable()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val logoContainer = findViewById<ImageView>(R.id.splash_logo)
		GlideApp.with(this)
			.load(R.drawable.logo_loading)
			.into(logoContainer)

		val authViewModel = ViewModelProvider(this@SplashActivity,
		                                      injector.authViewModelFactory())
			.get(AuthViewModel::class.java)

		disposables.add(authViewModel.isAuthenticated()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if (it == false) {
		                       Log.wtf(TAG, "USER IS NOT LOGGED IN")
		                       Handler().postDelayed({ startAuthActivity() }, 2000)

	                       }
	                       else {
		                       Log.wtf(TAG, "USER IS LOGGED IN")
		                       Handler().postDelayed({ startMainActivity() }, 2000)
	                       }
                       },
                       {
                           Log.wtf(TAG, it)
                       }))
	}

	private fun startAuthActivity(){
		val authIntent = Intent(this@SplashActivity, AuthActivity::class.java)
		startActivity(authIntent)
		finish()
	}

	private fun startMainActivity(){
		val authIntent = Intent(this@SplashActivity, MainActivity::class.java)
		startActivity(authIntent)
		finish()
	}

	override fun onDestroy() {
		super.onDestroy()
		disposables.clear()
	}
}