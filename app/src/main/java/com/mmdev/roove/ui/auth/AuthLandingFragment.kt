/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

package com.mmdev.roove.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentAuthLandingBinding
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.utils.extensions.showToastText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthLandingFragment: BaseFragment<AuthViewModel, FragmentAuthLandingBinding>(
	layoutId = R.layout.fragment_auth_landing
) {
	
	override val mViewModel: AuthViewModel by viewModels()
	
	//Progress dialog for any authentication action
	private lateinit var mCallbackManager: CallbackManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mCallbackManager = CallbackManager.Factory.create()

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		btnFacebookLogin.fragment = this@AuthLandingFragment
		btnFacebookLogin.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {

			override fun onSuccess(loginResult: LoginResult) {
				mViewModel.signIn(loginResult.accessToken.token)
			}

			override fun onCancel() {}

			override fun onError(error: FacebookException) {
				view.context.showToastText("$error")
			}
		})
		btnFacebookLoginDelegate.setOnClickListener {
			sharedViewModel.logOut()
			btnFacebookLogin.performClick()
		}

		tvOpenPolicies.setOnClickListener {
			var url = getString(R.string.privacy_policy_url)
			if (!url.startsWith("http://") && !url.startsWith("https://"))
				url = "http://$url"
			val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
			startActivity(browserIntent)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		mCallbackManager.onActivityResult(requestCode, resultCode, data)
	}

}

