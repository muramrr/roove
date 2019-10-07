package com.mmdev.meetapp.ui.auth.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import co.ceryle.segmentedbutton.SegmentedButtonGroup
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.mmdev.business.user.model.User
import com.mmdev.meetapp.R
import com.mmdev.meetapp.core.injector
import com.mmdev.meetapp.ui.auth.viewmodel.AuthViewModel
import com.mmdev.meetapp.ui.custom.LoadingDialog
import com.mmdev.meetapp.ui.custom.ProgressButton
import com.mmdev.meetapp.ui.main.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


/**
 *
 */
class AuthActivity: AppCompatActivity(R.layout.activity_auth)  {

	//Progress dialog for any authentication action
	private lateinit var progressDialog: LoadingDialog

	private lateinit var mCallbackManager: CallbackManager

	private lateinit var userModel: User

	private lateinit var authViewModel: AuthViewModel
	private val authViewModelFactory = injector.authViewModelFactory()

	private val disposables = CompositeDisposable()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mCallbackManager = CallbackManager.Factory.create()
		setUpFacebookLoginButton()
		progressDialog = LoadingDialog(this)
		authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
	}

	private fun setUpFacebookLoginButton() {
		val facebookLogInButton: LoginButton = findViewById(R.id.facebook_login_button)
		val facebookLoginButtonDelegate: Button = findViewById(R.id.facebook_login_button_delegate)
		facebookLogInButton.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {

			override fun onSuccess(loginResult: LoginResult) {
				progressDialog.showDialog()
				disposables.add(authViewModel.signInWithFacebook(loginResult.accessToken.token)
	                .flatMap { user -> userModel = user
		                authViewModel.handleUserExistence(user.userId)
	                }
	                .observeOn(AndroidSchedulers.mainThread())

	                .subscribe({
		                           progressDialog.dismissDialog()
		                           startMainActivity()
	                           },
	                           {
		                           progressDialog.dismissDialog()
		                           showRegistrationDialog()
	                           }
	                ))
			}

			override fun onCancel() {}

			override fun onError(error: FacebookException) {}
		})
		facebookLoginButtonDelegate.setOnClickListener { facebookLogInButton.performClick() }
	}

	private fun showRegistrationDialog() {
		var gender = "male"
		var preferedGender = "male"
		val additionalRegDialog = Dialog(this)
		additionalRegDialog.setContentView(R.layout.dialog_registration)
		val window = additionalRegDialog.window
		additionalRegDialog.show()
		window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
		                  LinearLayout.LayoutParams.MATCH_PARENT)

		val sbgGender = additionalRegDialog
			.findViewById<SegmentedButtonGroup>(R.id.dialog_registr_sbg_gender)
		val sbgPrefgender = additionalRegDialog
			.findViewById<SegmentedButtonGroup>(R.id.dialog_registr_sbg_preferedgender)

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
		val progressButton: ProgressButton = additionalRegDialog.findViewById(R.id.diag_reg_btn_done)
		progressButton.setOnClickListener {
			progressButton.startAnim()
			userModel.gender = gender
			userModel.preferedGender = preferedGender
			disposables.add(authViewModel.signUp(userModel)
				                .observeOn(AndroidSchedulers.mainThread())
				                .subscribe(
						                {
							                progressButton.stopAnim {
								                if(additionalRegDialog.isShowing)
									                additionalRegDialog.dismiss()
								                startMainActivity()
							                }
						                },
						                {
							                Log.wtf("mylogs", it)
						                })
			)
		}
	}

	private fun startMainActivity() {
		val mMainActivityIntent = Intent(this@AuthActivity, MainActivity::class.java)
		startActivity(mMainActivityIntent)
		finish()
	}


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		mCallbackManager.onActivityResult(requestCode, resultCode, data)
	}

	override fun onDestroy() {
		super.onDestroy()
		disposables.dispose()
	}
}

