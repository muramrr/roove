package com.mmdev.meetapp.ui.auth;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mmdev.meetapp.R;
import com.mmdev.meetapp.models.ProfileModel;
import com.mmdev.meetapp.services.GPSTracker;
import com.mmdev.meetapp.ui.main.MainActivity;
import com.mmdev.meetapp.ui.main.ProfileViewModel;
import com.mmdev.meetapp.utils.uiUtils;
import com.mmdev.progressbuttonlib.ProgressButton;

import java.util.ArrayList;


/**
 *
 */
public class AuthActivity extends AppCompatActivity
{

	private static final String TAG = "logi";

	@BindView(R.id.facebook_login_button)
	LoginButton facebookLogInButton;

	@BindView(R.id.facebook_login_button_delegate)
	Button facebookLoginButtonDelegate;

	private FirebaseAuth mFirebaseAuth;
	private FirebaseAuth.AuthStateListener mAuthStateListener;
	private FirebaseFirestore mFirestore;

	private FirebaseUser mFirebaseUser;
	//Progress dialog for any authentication action
	private AlertDialog progressDialog;

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
		mFirebaseAuth = FirebaseAuth.getInstance();
		mAuthStateListener = ((@NonNull FirebaseAuth firebaseAuth) -> {});
		mFirestore = FirebaseFirestore.getInstance();
		mCallbackManager = CallbackManager.Factory.create();
		setUpFacebookLoginButton();
		setProgressDialog();
		profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

	}

	private void setUpFacebookLoginButton() {
		facebookLogInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess (LoginResult loginResult) { handleFacebookAccessToken(loginResult.getAccessToken()); }

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
		mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, ((@NonNull Task<AuthResult> task) -> {
			if (task.isSuccessful() && mFirebaseAuth.getCurrentUser() != null){
				mFirebaseUser = mFirebaseAuth.getCurrentUser();
				handleUserExistence(mFirebaseUser.getUid());
			}
			else uiUtils.showSafeToast(getApplicationContext(),"LogIn Aborted");
			dismissProgressDialog();
		}));
	}

	private void handleUserExistence (String uId){
		CollectionReference users = mFirestore.collection("users");
		users.document(uId).get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				DocumentSnapshot document = task.getResult();
				if (document != null && document.exists()) {
					mProfileModel = document.toObject(ProfileModel.class);
					profileViewModel.saveProfile(getApplicationContext(), mProfileModel);
					profileViewModel.setProfileModel(mProfileModel);
					Toast.makeText(getApplicationContext(), "Exist",Toast.LENGTH_SHORT).show();
					startMainActivity ();
				} else showRegistrationDialog();
			} else Toast.makeText(getApplicationContext(), "Can't connect to server",Toast.LENGTH_SHORT).show();
		});
	}

	private void showRegistrationDialog () {
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
		//city = gps.getCity(this);
		mCity = "Kyiv";
		ArrayList<String> urls = new ArrayList<>();
		urls.add(String.valueOf(mFirebaseUser.getPhotoUrl()));
		mProfileModel = new ProfileModel(mFirebaseUser.getDisplayName(), mCity, mGender,
		                                 mPreferedGender, String.valueOf(mFirebaseUser.getPhotoUrl()),
		                                 urls, mFirebaseUser.getUid());
		CollectionReference profiles = mFirestore.collection("users");
		profiles.document(mFirebaseUser.getUid()).set(mProfileModel).addOnSuccessListener(aVoid -> {
			profileViewModel.saveProfile(this, mProfileModel);
			profileViewModel.setProfileModel(mProfileModel);
			Toast.makeText(this,
					"Successfully uploaded data to firebase + stored into sharedPrefs",
					Toast.LENGTH_LONG).show();
			pb_button.stopAnim(this::startMainActivity);
		});

	}

	private void startMainActivity () {
		Intent mMainActivityIntent = new Intent(AuthActivity.this, MainActivity.class);
		startActivity(mMainActivityIntent);
		if (additionalRegDialog!=null) additionalRegDialog.dismiss();
		finish();
	}

	/*
	progress dialog
	 */
	public void setProgressDialog() {
		int llPadding = 10;
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setPadding(llPadding, llPadding, llPadding, llPadding);
		ll.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		llParam.gravity = Gravity.CENTER;
		ll.setLayoutParams(llParam);
		
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setIndeterminate(true);
		progressBar.setPadding(llPadding, llPadding, llPadding, llPadding);
		progressBar.setLayoutParams(llParam);
		
		TextView tvText = new TextView(this);
		tvText.setText(getString(R.string.progress_dialog_text));
		tvText.setTextColor(Color.BLACK); //low api
		tvText.setTextSize(20);
		tvText.setLayoutParams(llParam);
		
		ll.addView(progressBar);
		ll.addView(tvText);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setView(ll);
		
		progressDialog = builder.create();
		
	}

	private void showProcessProgressDialog () {
		progressDialog.show();
		Window window = progressDialog.getWindow();
		if (window != null) {
			WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
			layoutParams.copyFrom(progressDialog.getWindow().getAttributes());
			layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
			layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			progressDialog.getWindow().setAttributes(layoutParams);
		}
	}

	private void dismissProgressDialog () { if (progressDialog.isShowing()) progressDialog.dismiss(); }

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart () {
		super.onStart();
		mFirebaseAuth.addAuthStateListener(mAuthStateListener);
	}

	@Override
	protected void onStop () {
		super.onStop();
		if (mAuthStateListener != null) mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
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

