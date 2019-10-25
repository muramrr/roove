package com.mmdev.roove.ui.auth.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.ceryle.segmentedbutton.SegmentedButtonGroup
import com.mmdev.roove.R
import com.mmdev.roove.ui.custom.ProgressButton

/* Created by A on 23.10.2019.*/

/**
 * This is the documentation block about the class
 */

class RegistrationFragment: Fragment(R.layout.activity_auth_fragment_reg){

	private lateinit var mAuthActivity: AuthActivity
	private var isRegistrationCompleted = false

	override fun onAttach(context: Context) {
		super.onAttach(context)
		activity?.let { mAuthActivity = it as AuthActivity }
		mAuthActivity.hideFacebookButton()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		var gender = "male"
		var preferedGender = "male"
		val sbgGender = view.findViewById(R.id.dialog_registr_sbg_gender) as SegmentedButtonGroup
		val sbgPrefgender = view.findViewById(R.id.dialog_registr_sbg_preferedgender) as SegmentedButtonGroup

		sbgGender.setOnClickedButtonListener {
			position -> gender = if (position == 0) "male" else "female"
		}

		sbgPrefgender.setOnClickedButtonListener { position ->
			when (position) {
				0 -> preferedGender = "male"
				1 -> preferedGender = "female"
				2 -> preferedGender = "both"
			}
		}
		val progressButton = view.findViewById(R.id.diag_reg_btn_done) as ProgressButton
		progressButton.setOnClickListener {
			isRegistrationCompleted = true
			progressButton.startAnim()
			mAuthActivity.fragmentRegistrationCallback(progressButton, gender, preferedGender)
		}
	}

	override fun onStop() {
		super.onStop()
		if (!isRegistrationCompleted) {
			mAuthActivity.fragmentNotSuccessfulRegistrationCallback()
			mAuthActivity.showFacebookButton()
		}
	}
}