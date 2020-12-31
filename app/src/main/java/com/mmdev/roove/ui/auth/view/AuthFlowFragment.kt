/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentAuthFlowBinding
import com.mmdev.roove.ui.common.base.FlowFragment

class AuthFlowFragment : FlowFragment<Nothing, FragmentAuthFlowBinding>(
	layoutId = R.layout.fragment_auth_flow
) {
	
	override val mViewModel: Nothing? = null
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val navHost = childFragmentManager
			.findFragmentById(R.id.authHostFragment) as NavHostFragment

		navController = navHost.findNavController()
	}
}

