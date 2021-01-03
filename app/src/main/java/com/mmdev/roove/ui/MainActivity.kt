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

package com.mmdev.roove.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
	
	companion object {
		private const val TAG = "mylogs_MainActivity"
		
		var currentUser: UserItem? = null
	}
	
	private val sharedViewModel: SharedViewModel by viewModels()
	

	override fun onCreate(savedInstanceState: Bundle?) {

		window.apply {
			clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
			addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			decorView.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			//status bar and navigation bar colors assigned in style file
		}
		
		super.onCreate(savedInstanceState)

		DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding

		val navController = findNavController(R.id.flowHostFragment)
		
		//start to listens auth status
		sharedViewModel.userState.observe(this, { userState ->
			userState.fold(
				authenticated = {
					currentUser = it
					navController.navigate(R.id.action_global_mainFlowFragment)
				},
				unauthenticated = {
					currentUser = null
					navController.navigate(R.id.action_global_authFlowFragment)
				},
				unregistered = {
					currentUser = null
					sharedViewModel.userInfoForRegistration.postValue(it)
					navController.navigate(R.id.action_global_registrationFlowFragment)
				}
			)
		})
		//for (i in 0 until Random.nextInt(5, 100)) {
		//	UtilityManager.generateFakeUsers()
		//}
		

		//note: debug
//		val dm = DisplayMetrics()
//		windowManager.defaultDisplay.getMetrics(dm)
//		val width = dm.widthPixels.toFloat()
//		val height = dm.heightPixels.toFloat()
//		Log.wtf(TAG, "${px2Dp(height)}")

	}

}
