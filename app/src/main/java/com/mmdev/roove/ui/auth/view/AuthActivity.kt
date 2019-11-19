/*
 * Created by Andrii Kovalchuk on 19.06.19 11:46
 * Copyright (c) 2019. All rights reserved.
 * Last modified 12.11.19 20:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.mmdev.business.user.model.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModel
import com.mmdev.roove.ui.custom.LoadingDialog
import com.mmdev.roove.ui.custom.ProgressButton
import com.mmdev.roove.ui.main.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity(R.layout.activity_auth)  {

	//Progress dialog for any authentication action
	private lateinit var progressDialog: LoadingDialog
	private lateinit var mCallbackManager: CallbackManager

	lateinit var userItemModel: UserItem

	private lateinit var authViewModel: AuthViewModel
	private val authViewModelFactory = injector.authViewModelFactory()

	private val disposables = CompositeDisposable()



	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mCallbackManager = CallbackManager.Factory.create()
		setUpFacebookLoginButton()
		progressDialog = LoadingDialog(this)
		authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
	}

	private fun setUpFacebookLoginButton() {
		val facebookLogInButton: LoginButton = findViewById(R.id.facebook_login_button)
		val facebookLoginButtonDelegate: Button = findViewById(R.id.facebook_login_button_delegate)
		facebookLogInButton.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {

			override fun onSuccess(loginResult: LoginResult) {
				disposables.add(authViewModel.signInWithFacebook(loginResult.accessToken.token)
	                .flatMap {
		                user -> userItemModel = user
		                authViewModel.handleUserExistence(user.userId)
	                }
	                .observeOn(AndroidSchedulers.mainThread())
	                .doOnSubscribe { progressDialog.showDialog() }
	                .doFinally { progressDialog.dismissDialog() }
	                .subscribe({
		                           startMainActivity()
	                           },
	                           {
		                           Toast.makeText(this@AuthActivity,
		                                          "User is not registered",
		                                          Toast.LENGTH_SHORT).show()
		                           startRegistrationFragment()
	                           }
	                ))
			}

			override fun onCancel() { authViewModel.logOut() }

			override fun onError(error: FacebookException) {}
		})
		facebookLoginButtonDelegate.setOnClickListener { facebookLogInButton.performClick() }
	}

	private fun startRegistrationFragment() {
		supportFragmentManager.beginTransaction().apply {
			setCustomAnimations(R.anim.enter_from_right,
			                    R.anim.exit_to_left,
			                    R.anim.enter_from_left,
			                    R.anim.exit_to_right)
			replace(R.id.auth_container, RegistrationFragment())
			addToBackStack(null)
			commit()
		}
	}

	fun fragmentRegistrationCallback(progressButton: ProgressButton,
	                                 gender:String,
	                                 preferedGender:String) {
		userItemModel.gender = gender
		userItemModel.preferedGender = preferedGender
		disposables.add(authViewModel.signUp(userItemModel)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
						progressButton.stopAnim (object: ProgressButton.OnStopAnim{
							override fun stop() {
								startMainActivity()
							}
						})
                       },
                       {
                       Toast.makeText(this,
                                      "$it",
                                      Toast.LENGTH_SHORT).show()
                       Log.wtf("mylogs", it)
                       }))
	}

	fun fragmentNotSuccessfulRegistrationCallback(){
		authViewModel.logOut()
	}

	fun showFacebookButton(){
		facebook_login_button_delegate.visibility = View.VISIBLE
		facebook_login_button_delegate.isClickable = true
	}

	fun hideFacebookButton(){
		facebook_login_button_delegate.visibility = View.INVISIBLE
		facebook_login_button_delegate.isClickable = false
	}

	private fun startMainActivity() {
		val mMainActivityIntent = Intent(this, MainActivity::class.java)
		startActivity(mMainActivityIntent)
		finish()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		mCallbackManager.onActivityResult(requestCode, resultCode, data)
	}

	override fun onDestroy() {
		super.onDestroy()
		disposables.dispose()
	}

}

