/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.12.20 21:34
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.base

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * Flow fragment correspond to host fragment switching by bottom navigation
 */

abstract class FlowFragment<VM: BaseViewModel, Binding: ViewDataBinding>(
	@LayoutRes layoutId: Int
) : BaseFragment<VM, Binding>(layoutId = layoutId)