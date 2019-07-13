package com.mmdev.meetups.ui.auth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mmdev.meetups.R;
import com.mmdev.meetups.models.ProfileModel;
import com.mmdev.meetups.services.GPSTracker;
import com.mmdev.meetups.ui.custom.ProgressButton;
import com.mmdev.meetups.ui.main.MainActivity;
import com.mmdev.meetups.ui.main.ProfileViewModel;
import com.mmdev.meetups.utils.uiUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;


/**
 * TODO: fix some bugs bonded with saved user in shared prefs and logging in same user
 * TODO: track if have user already used app
 */
public class AuthActivity extends AppCompatActivity
{

	private static final String TAG = "logi";

	@BindView(R.id.facebook_login_button)
	LoginButton facebookLogInButton;

	@BindView(R.id.facebook_login_button_delegate)
	Button facebookLoginButtonDelegate;

	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener authStateListener;

	private FirebaseUser firebaseUser;
	//Progress dialog for any authentication action
	private ProgressDialog progressDialog;

	CallbackManager mCallbackManager;

	private GPSTracker gps = new GPSTracker();
	private String mGender = "male";
	private String mPreferedGender = "male";
	private String mCity = "";
	private ProfileModel mProfileModel;

	private ProgressButton pb_button;
	private Dialog additionalRegDialog;

	private ProfileViewModel profileViewModel;

	@Override
	protected void onCreate (@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		ButterKnife.bind(this);
		initProgressDialog();
		profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
		firebaseAuth = FirebaseAuth.getInstance();
		authStateListener = ((@NonNull FirebaseAuth firebaseAuth) -> {
			firebaseUser = firebaseAuth.getCurrentUser();
			if (firebaseUser != null) showAdditionalInfoDialog();
		});
		mCallbackManager = CallbackManager.Factory.create();
		setUpFacebookLoginButton();
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Pass the activity result back to the Facebook SDK
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private void startMainActivity () {
		Intent mMainActivityIntent = new Intent(AuthActivity.this, MainActivity.class);
		startActivity(mMainActivityIntent);
		if (additionalRegDialog!=null) additionalRegDialog.dismiss();
		finish();
	}

	private void showAdditionalInfoDialog() {
		additionalRegDialog = new Dialog(this);
		additionalRegDialog.setContentView(R.layout.dialog_registration);
		Window window = additionalRegDialog.getWindow();
		additionalRegDialog.show();
		if (window != null)
			window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

		SegmentedButtonGroup sBG_gender = additionalRegDialog.findViewById(R.id.dialog_registr_sbg_gender);
		SegmentedButtonGroup sBG_prefGender = additionalRegDialog.findViewById(R.id.dialog_registr_sbg_preferedgender);
		sBG_gender.setOnClickedButtonListener(position -> {
			if (position == 0) mGender = "male";
			else mGender = "female";
		});
		sBG_prefGender.setOnClickedButtonListener(position -> {
			switch (position){
				case 0:
					mPreferedGender = "male";
					break;
				case 1:
					mPreferedGender = "female";
					break;
				case 2:
					mPreferedGender = "both";
					break;
			}
		});
		pb_button = additionalRegDialog.findViewById(R.id.diag_reg_btn_done);
		pb_button.setOnClickListener(v -> {
			//new AddUserToFirestore(this).execute();
            pb_button.startAnim();
            addUserToFirestore();
		});
	}

	private void addUserToFirestore () {
		firebaseUser = firebaseAuth.getCurrentUser();
		if (firebaseUser != null) {
			//mCity = gps.getCity(this);
			mCity = "Kyiv";
			ArrayList<String> urls = new ArrayList<>();
			urls.add(String.valueOf(firebaseUser.getPhotoUrl()));
			mProfileModel = new ProfileModel(firebaseUser.getDisplayName(), mCity,
					mGender, mPreferedGender, String.valueOf(firebaseUser.getPhotoUrl()), urls, firebaseUser.getUid());
			FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
			CollectionReference profiles = mFirestore.collection("gender")
					.document(mGender)
					.collection("users");
			profiles.document(firebaseUser.getUid()).set(mProfileModel)
					.addOnSuccessListener(aVoid -> {
						profileViewModel.saveProfile(this, mProfileModel);
						profileViewModel.setProfileModel(mProfileModel);
						Toast.makeText(this,
								"Successfully uploaded data to firebase + stored into sharedPrefs",
								Toast.LENGTH_LONG).show();
						pb_button.stopAnim(this::startMainActivity);
					});

			//profileViewModel.saveProfile(this, mProfileModel);
		}
	}

	private void setUpFacebookLoginButton() {
		facebookLogInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()
		{
			@Override
			public void onSuccess (LoginResult loginResult) {
				handleFacebookAccessToken(loginResult.getAccessToken()); }

			@Override
			public void onCancel () {}

			@Override
			public void onError (FacebookException error) {}
		});
		facebookLoginButtonDelegate.setOnClickListener((View v)-> facebookLogInButton.performClick());
	}

	private void handleFacebookAccessToken (AccessToken token) {
		showProcessProgressDialog();
		AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
		firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, ((@NonNull Task<AuthResult> task) -> {
			if (!task.isSuccessful())
				uiUtils.showSafeToast(getApplicationContext(),getString(R.string.login_aborted));
			dismissProgressDialog();
		}));
	}

	/*
	progress dialogs
	 */
	private void initProgressDialog () {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
	}

	private void showProcessProgressDialog () {
		progressDialog.setMessage(getString(R.string.please_wait));
		if (!progressDialog.isShowing()) progressDialog.show();
	}

	private void dismissProgressDialog ()
	{ if (progressDialog.isShowing()) progressDialog.dismiss(); }

	@Override
	protected void onStart () {
		super.onStart();
		firebaseAuth.addAuthStateListener(authStateListener);
	}

	@Override
	protected void onStop ()
	{
		super.onStop();
		if (authStateListener != null) firebaseAuth.removeAuthStateListener(authStateListener);
	}

	@Override
	protected void onDestroy () {
		super.onDestroy();
		if (gps!=null) gps.stopUsingGPS();
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
//			activityReference.get().pb_button.startAnim();
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
//			activityReference.get().pb_button.stopAnim(() -> activityReference.get().startMainActivity());
//		}
//	}

}

