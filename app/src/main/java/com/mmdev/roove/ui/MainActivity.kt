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

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.domain.user.data.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.log.logInfo
import com.mmdev.roove.core.permissions.AppPermission
import com.mmdev.roove.core.permissions.handlePermission
import com.mmdev.roove.core.permissions.onRequestPermissionsResultReceived
import com.mmdev.roove.core.permissions.requestAppPermissions
import com.mmdev.roove.databinding.ActivityMainBinding
import com.mmdev.roove.utils.extensions.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
	
	companion object {
		private const val TAG = "mylogs_MainActivity"
		
		var currentUser: UserItem? = null
			@Synchronized set
		
		/** The internal name of the provider for the coarse location  */
		private const val PROVIDER_COARSE = LocationManager.NETWORK_PROVIDER
		/** The internal name of the provider for the fine location  */
		private const val PROVIDER_FINE = LocationManager.GPS_PROVIDER
	}
	private val providersList = arrayOf(PROVIDER_COARSE, PROVIDER_FINE)
	private var isStarted = false
	
	/** The LocationManager instance used to query the device location  */
	private val mLocationManager: LocationManager by lazy {
		getSystemService(Context.LOCATION_SERVICE) as LocationManager
	}
	private val sharedViewModel: SharedViewModel by viewModels()
	
	private val navController by lazy {
		findNavController(R.id.flowHostFragment)
	}

	override fun onCreate(savedInstanceState: Bundle?) {

		WindowCompat.setDecorFitsSystemWindows(
			window.apply { addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) },
			false
		)
		
		super.onCreate(savedInstanceState)

		DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding
		
		observeUser()
		sharedViewModel.showErrorDialog(this, this)
	}
	
	override fun onStart() {
		if (!isStarted) {
			if (!hasLocationEnabled()) showLocationIsNotEnabled()
			else handleLocationPermission()
		}
		super.onStart()
	}
	
	//start to listens auth status
	private fun observeUser() = sharedViewModel.userState.observe(this) { userState ->
		userState.fold(authenticated = {
			logInfo(TAG, "$it")
			currentUser = it
			navController.navigate(R.id.action_global_mainFlowFragment)
			
			//generate new users todo: delete on release
			//for (i in 0 until 20) {
			//	sharedViewModel.updateUser(UtilityManager.generateFakeUser(user = it))
			//}
		}, unauthenticated = {
			currentUser = null
			navController.navigate(R.id.action_global_authFragment)
		}, unregistered = {
			currentUser = null
			sharedViewModel.userInfoForRegistration.postValue(it)
			navController.navigate(R.id.action_global_registrationFragment)
		})
	}
	
	/**
	 * Whether the device has location access enabled in the settings
	 *
	 * @return whether location access is enabled or not
	 */
	private fun hasLocationEnabled(): Boolean = providersList.any { hasLocationEnabled(it) }
	
	private fun hasLocationEnabled(providerName: String): Boolean = try {
		mLocationManager.isProviderEnabled(providerName)
	} catch (e: Exception) {
		false
	}
	
	/** Opens the device's settings screen where location access can be enabled */
	private fun openSettings() = startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
	
	private fun handleLocationPermission() = handlePermission(
		AppPermission.LOCATION,
		onGranted = { sharedViewModel.listenUserFlow() },
		onDenied = { requestAppPermissions(it) },
		onExplanationNeeded = { it.explanationMessageId }
	).also { isStarted = true }
	
	private fun showLocationIsNotEnabled() = MaterialAlertDialogBuilder(this)
		.setTitle(R.string.dialog_location_disabled_title)
		.setMessage(R.string.dialog_location_disabled_message)
		.setPositiveButton(R.string.dialog_location_btn_pos) { _, _ -> openSettings() }
		.setNegativeButton(R.string.dialog_location_btn_neg, null)
		.create()
		.show()
	
	
	// start after permissions was granted
	// If request is cancelled, the result arrays are empty.
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		onRequestPermissionsResultReceived(
			requestCode,
			grantResults,
			onPermissionGranted = { if (it == AppPermission.LOCATION) sharedViewModel.listenUserFlow() },
			onPermissionDenied = { it.deniedMessageId })
	}
	
}
