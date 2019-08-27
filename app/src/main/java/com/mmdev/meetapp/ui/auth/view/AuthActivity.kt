package com.mmdev.meetapp.ui.auth.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.domain.user.model.User
import com.mmdev.meetapp.R
import com.mmdev.meetapp.core.injector
import com.mmdev.meetapp.services.GPSTracker
import com.mmdev.meetapp.ui.MainActivity
import com.mmdev.meetapp.ui.ProfileViewModel
import com.mmdev.meetapp.ui.auth.viewmodel.AuthViewModel
import com.mmdev.meetapp.utils.uiUtils
import com.mmdev.progressbuttonlib.ProgressButton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*


/**
 *
 */
class AuthActivity: AppCompatActivity() {

	private val TAG = "logi"


	private lateinit var mFirebaseAuth: FirebaseAuth
	private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
	private var mFirestore: FirebaseFirestore? = null

	private lateinit var mFirebaseUser: FirebaseUser
	//Progress dialog for any authentication action
	private lateinit var progressDialog: AlertDialog

	private var mCallbackManager: CallbackManager? = null

	private val gps = GPSTracker()
	private var mGender = "male"
	private var mPreferedGender = "male"
	private var mCity = ""
	private lateinit var userModel: User

	private lateinit var progressButton: ProgressButton
	private lateinit var additionalRegDialog: Dialog

	private lateinit var profileViewModel: ProfileViewModel

	private lateinit var authViewModel: AuthViewModel
	private val authViewModelFactory = injector.authViewModelFactory()

	private val disposables = CompositeDisposable()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_auth)

		mFirebaseAuth = FirebaseAuth.getInstance()
		mAuthStateListener = FirebaseAuth.AuthStateListener {  }
		mFirestore = FirebaseFirestore.getInstance()
		mCallbackManager = CallbackManager.Factory.create()
		setUpFacebookLoginButton()
		setProgressDialog()
		authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
		profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

	}

	private fun setUpFacebookLoginButton() {
		val facebookLogInButton: LoginButton = findViewById(R.id.facebook_login_button)
		val facebookLoginButtonDelegate: Button = findViewById(R.id.facebook_login_button_delegate)
		facebookLogInButton.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {
			override fun onSuccess(loginResult: LoginResult) {
				handleFacebookAccessToken(loginResult.accessToken)
			}

			override fun onCancel() {}

			override fun onError(error: FacebookException) {}
		})
		facebookLoginButtonDelegate.setOnClickListener { facebookLogInButton.performClick() }
	}

	private fun handleFacebookAccessToken(token: AccessToken) {
		showProgressDialog()
		val credential = FacebookAuthProvider.getCredential(token.token)
		mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task: Task<AuthResult> ->
			if (task.isSuccessful && mFirebaseAuth.currentUser != null) {
				mFirebaseUser = mFirebaseAuth.currentUser!!
				disposables.add(authViewModel.handleUserExistence(mFirebaseUser.uid)
					                .observeOn(AndroidSchedulers.mainThread())
					                .subscribe( { user ->
						                            profileViewModel.saveProfile(applicationContext,
						                                                           user)
						                            profileViewModel.setProfileModel(user)
						                            dismissProgressDialog()
						                            startMainActivity()
					                            },
					                            {
						                            dismissProgressDialog()
						                            showRegistrationDialog()
					                            }
					                ))
			}
			else uiUtils.showSafeToast(applicationContext, "LogIn Aborted")

		}
	}

	private fun showRegistrationDialog() {
		additionalRegDialog = Dialog(this)
		additionalRegDialog.setContentView(R.layout.dialog_registration)
		val window = additionalRegDialog.window
		additionalRegDialog.show()
		window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

		val sbgGender = additionalRegDialog.findViewById<SegmentedButtonGroup>(R.id.dialog_registr_sbg_gender)
		val sbgPrefgender =
			additionalRegDialog.findViewById<SegmentedButtonGroup>(R.id.dialog_registr_sbg_preferedgender)

		sbgGender.setOnClickedButtonListener { position -> mGender = if (position == 0) "male" else "female" }

		sbgPrefgender.setOnClickedButtonListener { position ->
			when (position) {
				0 -> mPreferedGender = "male"
				1 -> mPreferedGender = "female"
				2 -> mPreferedGender = "both"
			}
		}
		progressButton = additionalRegDialog.findViewById(R.id.diag_reg_btn_done)
		progressButton.setOnClickListener {
			progressButton.startAnim()
			userModel = setupUser()
			disposables.add(authViewModel.signUp(userModel)
				                .observeOn(AndroidSchedulers.mainThread())
				                .subscribe( { progressButton.stopAnim { startMainActivity()} },
				                            { uiUtils.showSafeToast(applicationContext, "LogIn " +
				                                                                        "Aborted") } ))
		}
	}

	private fun setupUser(): User {
		//city = gps.getCity(this);
		mCity = "Kyiv"
		val urls = ArrayList<String>()
		urls.add(mFirebaseUser.photoUrl.toString())
		return User(mFirebaseUser.displayName!!,
		            mCity,
		            mGender,
		            mPreferedGender,
		            mFirebaseUser.photoUrl.toString(),
		            urls,
		            mFirebaseUser.uid)

			//
	}

	private fun startMainActivity() {
		val mMainActivityIntent = Intent(this@AuthActivity, MainActivity::class.java)
		if (additionalRegDialog.isShowing) additionalRegDialog.dismiss()
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
		mCallbackManager?.onActivityResult(requestCode, resultCode, data)
	}

	override fun onStart() {
		super.onStart()
		mFirebaseAuth.addAuthStateListener(mAuthStateListener!!)
	}

	override fun onStop() {
		super.onStop()
		if (mAuthStateListener != null) mFirebaseAuth.removeAuthStateListener(mAuthStateListener!!)
	}

	override fun onDestroy() {
		super.onDestroy()
		gps.stopUsingGPS()
	}


}

