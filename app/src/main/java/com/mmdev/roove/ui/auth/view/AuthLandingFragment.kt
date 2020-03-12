/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.03.20 15:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.mmdev.roove.R
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.common.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_auth_landing.*

class AuthLandingFragment: BaseFragment<AuthViewModel>(true, R.layout.fragment_auth_landing) {

	//Progress dialog for any authentication action
	private lateinit var mCallbackManager: CallbackManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mCallbackManager = CallbackManager.Factory.create()

		associatedViewModel = getViewModel()

		associatedViewModel.continueRegistration.observe(this, Observer {
			if (it == true) findNavController().navigate(R.id.action_auth_landing_to_registrationFragment)
		})

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		btnFacebookLogin.fragment = this
		btnFacebookLogin.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {

			override fun onSuccess(loginResult: LoginResult) {
				associatedViewModel.signIn(loginResult.accessToken.token)
			}

			override fun onCancel() {}

			override fun onError(error: FacebookException) {
				Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
			}
		})
		btnFacebookLoginDelegate.setOnClickListener {
			associatedViewModel.logOut()
			btnFacebookLogin.performClick()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		mCallbackManager.onActivityResult(requestCode, resultCode, data)
	}

}

