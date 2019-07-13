package com.mmdev.meetups.ui.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mmdev.meetups.R;
import com.mmdev.meetups.models.ProfileModel;
import com.mmdev.meetups.ui.activities.ProfileActivity;
import com.mmdev.meetups.ui.auth.AuthActivity;
import com.mmdev.meetups.ui.card.CardFragment;
import com.mmdev.meetups.ui.chat.ChatFragment;
import com.mmdev.meetups.ui.feed.FeedFragment;
import com.mmdev.meetups.ui.feed.FeedManager;
import com.mmdev.meetups.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{

	private static final String TAG = "myLogs";

	// Views UI
	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@BindView(R.id.nav_view)
	NavigationView navView;

	@BindView(R.id.drawer_layout)
	DrawerLayout drawerLayout;

	private TextView tvSignedInUserName;
	private TextView tvSignedInUserId;
	private ImageView ivSignedInUserAvatar;


	// Firebase
	private FirebaseAuth mFirebaseAuth;
	private FirebaseAuth.AuthStateListener mAuthStateListener;
	private FirebaseFirestore mFirestore;

	// Data
	public static List<ProfileModel> usersCards = new ArrayList<>();
	public ProfileModel profileModel;
	private FragmentManager mFragmentManager;
	private FeedManager mFeedManager = new FeedManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		setupNavigationView();
		mFragmentManager = getSupportFragmentManager();
		if (findViewById(R.id.main_container) != null) {
			if (savedInstanceState != null) return;
			mFragmentManager.beginTransaction().add(R.id.main_container, new FeedFragment()).commit();
		}
		mFirebaseAuth = FirebaseAuth.getInstance();
		checkConnection();
		mFirestore = FirebaseFirestore.getInstance();
		ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
		profileModel = profileViewModel.getProfileModel(this).getValue();
		setUpUser();
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();

	}

	/*
	 * generate random users to firestore
	 */
	private void onAddItemsClicked() {
		usersCards.clear();
		CollectionReference profiles = mFirestore.collection("gender");
		usersCards.addAll(FeedManager.generateUsers());
		for (ProfileModel i :usersCards)
			if (i.getGender().equals("female"))
				profiles.document("female")
						.collection("users")
						.document(i.getUserID()).set(i);
			else profiles.document("male")
					.collection("users")
					.document(i.getUserID()).set(i);


            /*
            generate likes/matches/skips lists
             */
//        profiles.document(mFirebaseAuth.getCurrentUser().getUid())
//                .collection("likes")
//                .document(mFirebaseAuth.getCurrentUser().getUid() + usersCards.get(1).getName())
//                .set(usersCards.get(1));
//        profiles.document(mFirebaseAuth.getCurrentUser().getUid())
//                .collection("matches")
//                .document(mFirebaseAuth.getCurrentUser().getUid() + usersCards.get(2).getName())
//                .set(usersCards.get(2));
//        profiles.document(mFirebaseAuth.getCurrentUser().getUid())
//                .collection("skips")
//                .document(mFirebaseAuth.getCurrentUser().getUid() + usersCards.get(3).getName())
//                .set(usersCards.get(3));
//
//        profiles.get().addOnCompleteListener(task -> {
//            String a;
//            if (task.isSuccessful())
//            {
//                a = task.getResult().getDocuments().get(0).get("Name").toString();
//                new Handler().postDelayed(() -> Toast.makeText(getApplicationContext(), "Name : " + String.valueOf(a), Toast.LENGTH_SHORT).show(), 1000);
//            }
//        });

	}

	/*
	 * check if user is authentificated
	 */
	private void checkConnection(){
		mAuthStateListener = ((@NonNull FirebaseAuth firebaseAuth) -> {
			FirebaseUser signedInUser = firebaseAuth.getCurrentUser();
			if (signedInUser == null) {
				Intent authIntent = new Intent(MainActivity.this, AuthActivity.class);
				startActivity(authIntent);
				finish();
			}
		});
	}

	private void setUpUser () {
		if (profileModel !=null){
			String mName = profileModel.getName();
			String mCity = profileModel.getCity();
			String mGender = profileModel.getGender();
			String mPreferedGender = profileModel.getPreferedGender();
			String signedInUserPhotoUrl = profileModel.getMainPhotoUrl();
			String uID = profileModel.getUserID();
			if (!TextUtils.isEmpty(signedInUserPhotoUrl))
				GlideApp.with(this).load(signedInUserPhotoUrl).into(ivSignedInUserAvatar);
			tvSignedInUserName.setText(mName);
			tvSignedInUserId.setText(uID);
		}
		else Toast.makeText(this,"No user login info", Toast.LENGTH_LONG).show();

	}

	private void setupNavigationView () {
		navView.setNavigationItemSelectedListener(navigationItemSelectedListener);
		navView.getChildAt((navView.getChildCount()-1)).setOverScrollMode(View.OVER_SCROLL_NEVER);
		View headerView = navView.getHeaderView(0);
		tvSignedInUserName = headerView.findViewById(R.id.signed_in_username_tv);
		ivSignedInUserAvatar = headerView.findViewById(R.id.signed_in_user_image_view);
		tvSignedInUserId = headerView.findViewById(R.id.user_facebookID);
		ImageView settingsButton = headerView.findViewById(R.id.settings_button);
		settingsButton.setOnClickListener((View v) -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
	}

	NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = ((@NonNull MenuItem item) -> {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		switch (id)
		{
			case (R.id.nav_feed):
				getPotentialUsersCards();
				break;
			case (R.id.nav_events):

				break;
			case (R.id.nav_post):
				//Toast.makeText(this,String.valueOf(usersCards),Toast.LENGTH_SHORT).show();
				onAddItemsClicked();
				break;
			case (R.id.nav_notifications):
				//Toast.makeText(this,String.valueOf(mFeedManager.getUsersCards()),Toast.LENGTH_SHORT).show();
				break;
			case (R.id.nav_account):
				//Toast.makeText(this, String.valueOf(FeedManager.generateUsers()), Toast.LENGTH_SHORT).show();
				break;
			case (R.id.nav_log_out):
				showSignOutPrompt();
				break;
		}
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawer(GravityCompat.START);
		return true;

	});

	private void startCardFragment(){
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right);
		ft.replace(R.id.main_container, new CardFragment(),"CardFragment");
		ft.addToBackStack(null);
		ft.commit();
	}

	private void startChatFragment(){
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right);
		ft.replace(R.id.main_container, new ChatFragment(), "ChatFragment");
		ft.addToBackStack(null);
		ft.commit();
	}

	private void showSignOutPrompt() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you wish to sign out?");
		builder.setPositiveButton("YES", ((DialogInterface dialog, int which) -> {
			dialog.dismiss();
			//Attempt sign out
			if (mFirebaseAuth.getCurrentUser() != null) {
				mFirebaseAuth.signOut();
				LoginManager.getInstance().logOut();
			}
		}));
		builder.setNegativeButton("NO", (DialogInterface dialog, int which) -> dialog.dismiss());
		builder.create().show();
	}

	/*
	menu init
	 */
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	menu button click handler
	 */
	public void MessagesClick (MenuItem item) {
		if(mFragmentManager.findFragmentByTag("ChatFragment")!=null)
			return;
		startChatFragment();
	}

	@Override
	public void onBackPressed() {
		drawerLayout = findViewById(R.id.drawer_layout);
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawer(GravityCompat.START);
		else
			super.onBackPressed();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mFirebaseAuth.addAuthStateListener(mAuthStateListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
	}

	/*
		getting users from firestore by prefered gender
		todo: move to another class this shit
	 */
	public void getPotentialUsersCards (){
		if(mFragmentManager.findFragmentByTag("CardFragment")!=null)
			return;
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
		progressDialog.show();
		usersCards.clear();
		String mPreferedGender = profileModel.getPreferedGender();
		CollectionReference genderProfiles = mFirestore.collection("gender")
				.document(mPreferedGender)
				.collection("users");
		genderProfiles.get().addOnCompleteListener(task -> {
			if (task.isSuccessful() && task.getResult()!=null) {
				QuerySnapshot result = task.getResult();
				for (DocumentSnapshot doc :result)
					if(!doc.getId().equals(profileModel.getUserID()))
						usersCards.add(doc.toObject(ProfileModel.class));
				if (progressDialog.isShowing())
					progressDialog.dismiss();
				startCardFragment();
			}
		})
				.addOnFailureListener(e -> Toast.makeText(this, "Cannot retrieve information", Toast.LENGTH_SHORT).show());

	}

}
