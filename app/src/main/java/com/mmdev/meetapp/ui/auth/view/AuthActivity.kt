package com.mmdev.meetapp.ui.auth.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.mmdev.meetapp.ui.main.view.MainActivity
import com.mmdev.progressbuttonlib.ProgressButton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


/**
 *
 */
class AuthActivity: AppCompatActivity(R.layout.activity_auth)  {

	//Progress dialog for any authentication action
	private lateinit var progressDialog: AlertDialog

	private lateinit var mCallbackManager: CallbackManager

	private lateinit var userModel: User

	private lateinit var authViewModel: AuthViewModel
	private val authViewModelFactory = injector.authViewModelFactory()

	private val disposables = CompositeDisposable()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mCallbackManager = CallbackManager.Factory.create()
		setUpFacebookLoginButton()
		setProgressDialog()
		authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
	}

	private fun setUpFacebookLoginButton() {
		val facebookLogInButton: LoginButton = findViewById(R.id.facebook_login_button)
		val facebookLoginButtonDelegate: Button = findViewById(R.id.facebook_login_button_delegate)
		facebookLogInButton.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {

			override fun onSuccess(loginResult: LoginResult) {
				showProgressDialog()
				disposables.add(authViewModel.signInWithFacebook(loginResult.accessToken.token)
	                .flatMap { user -> userModel = user
		                authViewModel.handleUserExistence(user.userId)
	                }
	                .observeOn(AndroidSchedulers.mainThread())

	                .subscribe({
		                           dismissProgressDialog()
		                           startMainActivity()
	                           },
	                           {
		                           dismissProgressDialog()
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

	/*
	progress dialog
	 */
	private fun setProgressDialog() {
		val llPadding = 10
		val ll = LinearLayout(this)
		ll.orientation = LinearLayout.HORIZONTAL
		ll.setPadding(llPadding, llPadding, llPadding, llPadding)
		ll.gravity = Gravity.CENTER
		val llParam =
			LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
		llParam.gravity = Gravity.CENTER
		ll.layoutParams = llParam

		val progressBar = ProgressBar(this)
		progressBar.isIndeterminate = true
		progressBar.setPadding(llPadding, llPadding, llPadding, llPadding)
		progressBar.layoutParams = llParam

		val tvText = TextView(this)
		tvText.text = getString(R.string.progress_dialog_text)
		tvText.setTextColor(Color.BLACK) //low api
		tvText.textSize = 20f
		tvText.layoutParams = llParam

		ll.addView(progressBar)
		ll.addView(tvText)

		val builder = AlertDialog.Builder(this)
		builder.setCancelable(true)
		builder.setView(ll)

		progressDialog = builder.create()

	}

	private fun showProgressDialog() {
		progressDialog.show()
		val window = progressDialog.window
		if (window != null) {
			val layoutParams = WindowManager.LayoutParams()
			layoutParams.copyFrom(progressDialog.window!!.attributes)
			layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
			layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
			progressDialog.window!!.attributes = layoutParams
		}
	}

	private fun dismissProgressDialog() {
		if (progressDialog.isShowing) progressDialog.dismiss()
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

