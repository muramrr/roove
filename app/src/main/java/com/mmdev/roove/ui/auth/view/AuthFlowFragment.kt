/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 05.12.19 19:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.mmdev.business.user.model.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.custom.ProgressButton
import kotlinx.android.synthetic.main.fragment_flow_auth.*

class AuthFlowFragment : Fragment(R.layout.fragment_flow_auth)  {


	//Progress dialog for any authentication action
	private lateinit var mCallbackManager: CallbackManager

	var userItemModel: UserItem = UserItem()

	private lateinit var authViewModel: AuthViewModel
	private val factory = injector.factory()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mCallbackManager = CallbackManager.Factory.create()
		authViewModel = activity?.run {
			ViewModelProvider(this, factory)[AuthViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		authViewModel.continueRegistration.observe(this, Observer {
			if (it == true) startRegistrationFragment()
		})
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setUpFacebookLoginButton()
	}

	private fun setUpFacebookLoginButton() {
		btnFacebookLogin.fragment = this
		btnFacebookLogin.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {

			override fun onSuccess(loginResult: LoginResult) {
				authViewModel.signInWithFacebook(loginResult.accessToken.token)
			}

			override fun onCancel() { authViewModel.logOut() }

			override fun onError(error: FacebookException) {}
		})
		btnFacebookLoginDelegate.setOnClickListener { btnFacebookLogin.performClick() }
	}

	private fun startRegistrationFragment() {
		childFragmentManager.beginTransaction().apply {
			setCustomAnimations(R.anim.enter_from_right,
			                    R.anim.exit_to_left,
			                    R.anim.enter_from_left,
			                    R.anim.exit_to_right)
			add(R.id.authContainer, RegistrationFragment())
			addToBackStack(null)
			commit()
		}
	}

	fun fragmentRegistrationCallback(progressButton: ProgressButton,
	                                 gender:String,
	                                 preferedGender:String) {
		userItemModel.gender = gender
		userItemModel.preferedGender = preferedGender
		authViewModel.signUp(userItemModel)
	}

	fun fragmentNotSuccessfulRegistrationCallback(){
		authViewModel.logOut()
	}

	fun showFacebookButton(){
		btnFacebookLoginDelegate.visibility = View.VISIBLE
		btnFacebookLoginDelegate.isClickable = true
	}

	fun hideFacebookButton(){
		btnFacebookLoginDelegate.visibility = View.INVISIBLE
		btnFacebookLoginDelegate.isClickable = false
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		mCallbackManager.onActivityResult(requestCode, resultCode, data)
	}

}

