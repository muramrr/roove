/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 17:56
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp
import kotlinx.android.synthetic.main.fragment_splash.*

/**
 * This is the documentation block about the class
 */

class SplashFragment: Fragment(R.layout.fragment_splash) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		GlideApp.with(ivSplashLogo.context)
			.asGif()
			.load(R.drawable.logo_loading)
			.into(ivSplashLogo)
	}
}