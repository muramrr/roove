package com.mmdev.meetapp.ui.main;

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
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mmdev.meetapp.R;
import com.mmdev.meetapp.models.ProfileModel;
import com.mmdev.meetapp.ui.activities.ProfileActivity;
import com.mmdev.meetapp.ui.auth.AuthActivity;
import com.mmdev.meetapp.ui.card.CardFragment;
import com.mmdev.meetapp.ui.chat.ChatFragment;
import com.mmdev.meetapp.ui.feed.FeedFragment;
import com.mmdev.meetapp.ui.feed.FeedManager;
import com.mmdev.meetapp.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
	private static final String TAG = "myLogs";

	// Views UI
	@BindView(R.id.toolbar)
	Toolbar toolbar;

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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		navView = findViewById(R.id.nav_view);
		mFirebaseAuth = FirebaseAuth.getInstance();
		checkConnection();
		setSupportActionBar(toolbar);
		setupNavigationView();
		mFragmentManager = getSupportFragmentManager();
		if (findViewById(R.id.main_container) != null) {
			if (savedInstanceState != null) return;
			mFragmentManager.beginTransaction()
					.add(R.id.main_container, new FeedFragment(), "FeedFragment").commit();
		}
		mFirestore = FirebaseFirestore.getInstance();
		ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
		profileModel = profileViewModel.getProfileModel(this).getValue();
		setUpUser();
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
				                          drawerLayout,
				                          toolbar,
				                          R.string.navigation_drawer_open,
				                          R.string.navigation_drawer_close);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();

	}

	/*
	 * check if user is authentificated
	 */
	private void checkConnection(){
		mAuthStateListener = ((@NonNull FirebaseAuth firebaseAuth) -> {
			if (firebaseAuth.getCurrentUser() == null) {
				Intent authIntent = new Intent(MainActivity.this, AuthActivity.class);
				startActivity(authIntent);
				finish();
			}
		});
	}

	private void setUpUser () {
		if (profileModel != null){
			String mName = profileModel.getName();
			String mCity = profileModel.getCity();
			String mGender = profileModel.getGender();
			String mPreferedGender = profileModel.getPreferedGender();
			String signedInUserPhotoUrl = profileModel.getMainPhotoUrl();
			String uID = profileModel.getUserId();
			if (!TextUtils.isEmpty(signedInUserPhotoUrl))
				GlideApp.with(this).load(signedInUserPhotoUrl).into(ivSignedInUserAvatar);
			tvSignedInUserName.setText(mName);
			tvSignedInUserId.setText(uID);
		}
		else Toast.makeText(this,"No user login info", Toast.LENGTH_LONG).show();

	}

	private void setupNavigationView () {
		navView.setNavigationItemSelectedListener(item -> {
			// Handle navigation view item clicks here.
			int id = item.getItemId();
			switch (id) {
				case (R.id.nav_feed):
					startCardFragment();
					break;
				case (R.id.nav_events):
					
					break;
				case (R.id.nav_post):
					//Toast.makeText(this,String.valueOf(usersCards),Toast.LENGTH_SHORT).show();
					onGenerateUsers();
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
		navView.getChildAt((navView.getChildCount()-1)).setOverScrollMode(View.OVER_SCROLL_NEVER);
		View headerView = navView.getHeaderView(0);
		tvSignedInUserName = headerView.findViewById(R.id.signed_in_username_tv);
		ivSignedInUserAvatar = headerView.findViewById(R.id.signed_in_user_image_view);
		tvSignedInUserId = headerView.findViewById(R.id.user_facebookID);
		ImageView settingsButton = headerView.findViewById(R.id.settings_button);
		settingsButton.setOnClickListener((View v) -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
	}
	
	/*
	 * generate random users to firestore
	 */
	private void onGenerateUsers ()  {
		usersCards.clear();
		CollectionReference usersCollection = mFirestore.collection("users");
		usersCards.addAll(FeedManager.generateUsers());
		for (ProfileModel i :usersCards)
			usersCollection.document(i.getUserId()).set(i);


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
	 * start card swipe
	 */
	private void startCardFragment(){
		if(mFragmentManager.findFragmentByTag("CardFragment") != null) return;
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right);
		ft.replace(R.id.main_container, new CardFragment(),"CardFragment");
		ft.addToBackStack(null);
		ft.commit();
	}

	/*
	 * start chat
	 */
	private void startChatFragment(){
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right);
		ft.replace(R.id.main_container, new ChatFragment(), "MessagesFragment");
		ft.addToBackStack(null);
		ft.commit();
	}

	private void showSignOutPrompt() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you wish to sign out?");
		builder.setPositiveButton("YES", ((DialogInterface dialog, int which) -> {
			dialog.dismiss();
			//Attempt sign out
			mFirebaseAuth.signOut();
			LoginManager.getInstance().logOut();
			
		}));
		builder.setNegativeButton("NO", (DialogInterface dialog, int which) -> dialog.dismiss());
		builder.create().show();
	}

	/*
	menu init
	 */
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	menu button click handler
	 */
	public void MessagesClick (MenuItem item) {
		if(mFragmentManager.findFragmentByTag("MessagesFragment") != null) return;
		startChatFragment();
	}

	@Override
	public void onBackPressed() {
		drawerLayout = findViewById(R.id.drawer_layout);
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
		else super.onBackPressed();
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
	

}
