/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 06.12.19 21:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core

import androidx.fragment.app.Fragment

/**
 * This is the documentation block about the class
 */

abstract class FlowFragment(layoutRes: Int = 0): Fragment(layoutRes)  {

	open fun onBackPressed(){}

}