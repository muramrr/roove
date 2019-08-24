package com.mmdev.meetapp.ui.auth

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
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
import com.mmdev.meetapp.R
import com.mmdev.meetapp.models.ProfileModel
import com.mmdev.meetapp.services.GPSTracker
import com.mmdev.meetapp.ui.MainActivity
import com.mmdev.meetapp.ui.main.ProfileViewModel
import com.mmdev.meetapp.utils.uiUtils
import com.mmdev.progressbuttonlib.ProgressButton
import java.util.*


/**
 *
 */
class AuthActivity: AppCompatActivity() {

	private val TAG = "logi"


	private var facebookLogInButton: LoginButton? = null
	private var facebookLoginButtonDelegate: Button? = null

	private var mFirebaseAuth: FirebaseAuth? = null
	private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
	private var mFirestore: FirebaseFirestore? = null

	private var mFirebaseUser: FirebaseUser? = null
	//Progress dialog for any authentication action
	private var progressDialog: AlertDialog? = null

	private var mCallbackManager: CallbackManager? = null

	private val gps = GPSTracker()
	private var mGender = "male"
	private var mPreferedGender = "male"
	private var mCity = ""
	private var mProfileModel: ProfileModel? = null

	private var progressButton: ProgressButton? = null
	private var additionalRegDialog: Dialog? = null

	private var profileViewModel: ProfileViewModel? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_auth)
		facebookLogInButton = findViewById(R.id.facebook_login_button)
		facebookLoginButtonDelegate = findViewById(R.id.facebook_login_button_delegate)

		mFirebaseAuth = FirebaseAuth.getInstance()
		mAuthStateListener = FirebaseAuth.AuthStateListener {  }
		mFirestore = FirebaseFirestore.getInstance()
		mCallbackManager = CallbackManager.Factory.create()
		setUpFacebookLoginButton()
		setProgressDialog()
		profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

	}

	private fun setUpFacebookLoginButton() {
		facebookLogInButton!!.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {
			override fun onSuccess(loginResult: LoginResult) {
				handleFacebookAccessToken(loginResult.accessToken)
			}

			override fun onCancel() {}

			override fun onError(error: FacebookException) {}
		})
		facebookLoginButtonDelegate!!.setOnClickListener { facebookLogInButton!!.performClick() }
	}

	private fun handleFacebookAccessToken(token: AccessToken) {
		showProcessProgressDialog()
		val credential = FacebookAuthProvider.getCredential(token.token)
		mFirebaseAuth!!.signInWithCredential(credential).addOnCompleteListener(this) { task: Task<AuthResult> ->
			if (task.isSuccessful && mFirebaseAuth!!.currentUser != null) {
				mFirebaseUser = mFirebaseAuth!!.currentUser
				handleUserExistence(mFirebaseUser!!.uid)
			}
			else uiUtils.showSafeToast(applicationContext, "LogIn Aborted")
			dismissProgressDialog()
		}
	}

	private fun handleUserExistence(uId: String) {
		val users = mFirestore!!.collection("users")
		users.document(uId).get().addOnCompleteListener { task ->
			if (task.isSuccessful) {
				val document = task.result
				if (document != null && document.exists()) {
					mProfileModel = document.toObject(ProfileModel::class.java)
					profileViewModel!!.saveProfile(applicationContext, mProfileModel)
					profileViewModel!!.setProfileModel(mProfileModel)
					Toast.makeText(applicationContext, "Exist", Toast.LENGTH_SHORT).show()
					startMainActivity()
				}
				else showRegistrationDialog()
			}
			else Toast.makeText(applicationContext, "Can't connect to server", Toast.LENGTH_SHORT).show()
		}
	}

	private fun showRegistrationDialog() {
		additionalRegDialog = Dialog(this)
		additionalRegDialog!!.setContentView(R.layout.dialog_registration)
		val window = additionalRegDialog!!.window
		additionalRegDialog!!.show()
		window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

		val sbgGender = additionalRegDialog!!.findViewById<SegmentedButtonGroup>(R.id.dialog_registr_sbg_gender)
		val sbgPrefgender =
			additionalRegDialog!!.findViewById<SegmentedButtonGroup>(R.id.dialog_registr_sbg_preferedgender)

		sbgGender.setOnClickedButtonListener { position -> mGender = if (position == 0) "male" else "female" }

		sbgPrefgender.setOnClickedButtonListener { position ->
			when (position) {
				0 -> mPreferedGender = "male"
				1 -> mPreferedGender = "female"
				2 -> mPreferedGender = "both"
			}
		}
		progressButton = additionalRegDialog!!.findViewById(R.id.diag_reg_btn_done)
		progressButton!!.setOnClickListener {
			//new AddUserToFirestore(this).execute();
			progressButton!!.startAnim()
			addUserToFirestore()
		}
	}

	private fun addUserToFirestore() {
		//city = gps.getCity(this);
		mCity = "Kyiv"
		val urls = ArrayList<String>()
		urls.add(mFirebaseUser!!.photoUrl.toString())
		mProfileModel = ProfileModel(mFirebaseUser!!.displayName!!,
		                             mCity,
		                             mGender,
		                             mPreferedGender,
		                             mFirebaseUser!!.photoUrl.toString(),
		                             urls,
		                             mFirebaseUser!!.uid)
		val profiles = mFirestore!!.collection("users")
		profiles.document(mFirebaseUser!!.uid).set(mProfileModel!!).addOnSuccessListener {
			profileViewModel!!.saveProfile(this, mProfileModel)
			profileViewModel!!.setProfileModel(mProfileModel)
			Toast.makeText(this,
			               "Successfully uploaded data to firebase + stored into sharedPrefs",
			               Toast.LENGTH_LONG).show()
			progressButton!!.stopAnim { startMainActivity() }
		}

	}

	private fun startMainActivity() {
		val mMainActivityIntent = Intent(this@AuthActivity, MainActivity::class.java)
		startActivity(mMainActivityIntent)
		if (additionalRegDialog != null) additionalRegDialog!!.dismiss()
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

	private fun showProcessProgressDialog() {
		progressDialog!!.show()
		val window = progressDialog!!.window
		if (window != null) {
			val layoutParams = WindowManager.LayoutParams()
			layoutParams.copyFrom(progressDialog!!.window!!.attributes)
			layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
			layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
			progressDialog!!.window!!.attributes = layoutParams
		}
	}

	private fun dismissProgressDialog() {
		if (progressDialog!!.isShowing) progressDialog!!.dismiss()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		mCallbackManager?.onActivityResult(requestCode, resultCode, data)
	}

	override fun onStart() {
		super.onStart()
		mFirebaseAuth!!.addAuthStateListener(mAuthStateListener!!)
	}

	override fun onStop() {
		super.onStop()
		if (mAuthStateListener != null) mFirebaseAuth!!.removeAuthStateListener(mAuthStateListener!!)
	}

	override fun onDestroy() {
		super.onDestroy()
		gps.stopUsingGPS()
	}


	//adding user to firestore in other thread
	//	private static class AddUserToFirestore extends AsyncTask<Void, Void, Void> {
	//		private WeakReference<AuthActivity> activityReference;
	//
	//		// only retain a weak reference to the activity
	//		AddUserToFirestore(AuthActivity context) {
	//			activityReference = new WeakReference<>(context);
	//		}
	//
	//		@Override
	//		protected void onPreExecute () {
	//			super.onPreExecute();
	//			activityReference.get().progressButton.startAnim();
	//		}
	//
	//		@Override
	//		protected Void doInBackground(Void... params) {
	//			activityReference.get().addUserToFirestore();
	//			return null;
	//		}
	//
	//		@Override
	//		protected void onPostExecute (Void aVoid) {
	//			super.onPostExecute(aVoid);
	//			activityReference.get().progressButton.stopAnim(() -> activityReference.get().startMainActivity());
	//		}
	//	}

}

