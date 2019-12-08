/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 08.12.19 16:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core

import androidx.fragment.app.Fragment
import com.mmdev.roove.core.injector

/**
 * This is the documentation block about the class
 */

abstract class BaseFragment (layoutRes: Int = 0) : Fragment(layoutRes) {

	val factory = injector.factory()

	open fun onBackPressed() {}


}