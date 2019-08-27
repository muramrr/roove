package com.mmdev.meetapp.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mmdev.domain.user.model.User
import com.mmdev.meetapp.R
import com.mmdev.meetapp.ui.auth.view.AuthActivity
import com.mmdev.meetapp.ui.card.CardFragment
import com.mmdev.meetapp.ui.chat.view.ChatFragment
import com.mmdev.meetapp.ui.feed.FeedFragment
import com.mmdev.meetapp.utils.GlideApp

class MainActivity: AppCompatActivity(), MainActivityListeners  {

	private val TAG = "myLogs"

	private lateinit var navView: NavigationView
	private lateinit var toolbar: Toolbar

	private lateinit var drawerLayout: DrawerLayout

	private lateinit var tvSignedInUserName: TextView
	private lateinit var tvSignedInUserId: TextView
	private lateinit var ivSignedInUserAvatar: ImageView


	// Firebase
	private lateinit var mFirebaseAuth: FirebaseAuth
	private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
	lateinit var userModel: User
	private lateinit var mFragmentManager: FragmentManager


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)
		setUpNavigationView()
		mFragmentManager = supportFragmentManager
		showFeedFragment()
		mFirebaseAuth = FirebaseAuth.getInstance()
		checkConnection()


		val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
		userModel = profileViewModel.getProfileModel(this).value!!
		setUpUser()

	}

	override fun onCardsClick() = startCardFragment()
	//todo: change this to messages fragment
	override fun onMessagesClick(username: String) = startChatFragment(username)

	override fun onLogOutClick() = showSignOutPrompt()

	/*
	 * start card swipe
	 */
	private fun startCardFragment() {
		mFragmentManager.findFragmentByTag("CardFragment") ?: mFragmentManager
			.beginTransaction().apply{
				setCustomAnimations(R.anim.fragment_enter_from_right,
				                    R.anim.fragment_exit_to_left,
				                    R.anim.fragment_enter_from_left,
				                    R.anim.fragment_exit_to_right)
				replace(R.id.main_container, CardFragment(), "CardFragment")
				addToBackStack(null)
				commit()
			}

	}

	/*
	 * start chat
	 */
	private fun startChatFragment(username: String) {
		mFragmentManager.findFragmentByTag("ChatFragment") ?: mFragmentManager
			.beginTransaction().apply {
				setCustomAnimations(R.anim.fragment_enter_from_right,
				                    R.anim.fragment_exit_to_left,
				                    R.anim.fragment_enter_from_left,
				                    R.anim.fragment_exit_to_right)
				replace(R.id.main_container, ChatFragment.newInstance(username), "ChatFragment")
				addToBackStack(null)
				commit()
			}
	}

	/*
	* log out pop up
	*/
	private fun showSignOutPrompt() {
		val builder = AlertDialog.Builder(this)
		builder.setMessage("Do you wish to sign out?")
		builder.setPositiveButton("YES") { dialog: DialogInterface, _: Int ->
			dialog.dismiss()
			//Attempt sign out
			mFirebaseAuth.signOut()
			LoginManager.getInstance().logOut()

		}
		builder.setNegativeButton("NO") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
		builder.create().show()
	}

	/*
	 * check if user is authentificated
	 */
	private fun checkConnection() {
		mAuthStateListener = FirebaseAuth.AuthStateListener{
			if (it.currentUser == null) {
				val authIntent = Intent(this@MainActivity, AuthActivity::class.java)
				startActivity(authIntent)
				finish()
			}
		}
	}

	private fun setUpUser() {
		val mName = userModel.name
		val mCity = userModel.city
		val mGender = userModel.gender
		val mPreferedGender = userModel.preferedGender
		val signedInUserPhotoUrl = userModel.mainPhotoUrl
		val uID = userModel.userId
		if (!TextUtils.isEmpty(signedInUserPhotoUrl))
			GlideApp.with(this).load(signedInUserPhotoUrl).into(ivSignedInUserAvatar)
		tvSignedInUserName.text = mName
		tvSignedInUserId.text = uID

	}


	private fun setUpNavigationView() {
		navView = findViewById(R.id.nav_view)
		drawerLayout = findViewById(R.id.drawer_layout)
		val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
		                                   R.string.navigation_drawer_open,
		                                   R.string.navigation_drawer_close)

		drawerLayout.addDrawerListener(toggle)
		toggle.syncState()
		navView.getChildAt(navView.childCount - 1).overScrollMode = View.OVER_SCROLL_NEVER
		val headerView = navView.getHeaderView(0)
		tvSignedInUserName = headerView.findViewById(R.id.signed_in_username_tv)
		ivSignedInUserAvatar = headerView.findViewById(R.id.signed_in_user_image_view)
		tvSignedInUserId = headerView.findViewById(R.id.user_facebookID)

		navView.setNavigationItemSelectedListener { item ->
			drawerLayout.closeDrawer(GravityCompat.START)
			// Handle navigation view item clicks here.
			when (item.itemId) {
				R.id.nav_feed -> onCardsClick()
				R.id.nav_events -> { }
				R.id.nav_post -> {}
				R.id.nav_notifications -> { }
				R.id.nav_account -> { }
				R.id.nav_log_out -> onLogOutClick()
			}
			return@setNavigationItemSelectedListener true
		}

	}

	private fun showFeedFragment(){
		mFragmentManager.beginTransaction().apply {
			add(R.id.main_container, FeedFragment(), "FeedFragment")
			commit()
		}
	}

	/*
	 * generate random users to firestore
	 */
//	private fun onGenerateUsers() {
//		usersCards.clear()
//		val usersCollection = mFirestore!!.collection("users")
//		usersCards.addAll(FeedManager.generateUsers())
//		for (i in usersCards) usersCollection.document(i.userId).set(i)
//
//
//		/*
//            generate likes/matches/skips lists
//             */
//		        profiles.document(mFirebaseAuth.getCurrentUser().getUid())
//		                .collection("likes")
//		                .document(mFirebaseAuth.getCurrentUser().getUid() + usersCards.get(1).getName())
//		                .set(usersCards.get(1));
//		        profiles.document(mFirebaseAuth.getCurrentUser().getUid())
//		                .collection("matches")
//		                .document(mFirebaseAuth.getCurrentUser().getUid() + usersCards.get(2).getName())
//		                .set(usersCards.get(2));
//		        profiles.document(mFirebaseAuth.getCurrentUser().getUid())
//		                .collection("skips")
//		                .document(mFirebaseAuth.getCurrentUser().getUid() + usersCards.get(3).getName())
//		                .set(usersCards.get(3));
//
//		        profiles.get().addOnCompleteListener(task -> {
//		            String a;
//		            if (task.isSuccessful())
//		            {
//		                a = task.getResult().getDocuments().get(0).get("Name").toString();
//		                new Handler().postDelayed(() -> Toast.makeText(getApplicationContext(), "Name : " + String.valueOf(a), Toast.LENGTH_SHORT).show(), 1000);
//		            }
//		        });
//
//	}

	/*
	menu init
	 */
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	/*
	menu button click handler
	 */
	fun messagesMenuClick(item: MenuItem) {
		//startMessagesFragment()
	}

	override fun onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
		else super.onBackPressed()
	}

	override fun onStart() {
		super.onStart()
		mFirebaseAuth.addAuthStateListener(mAuthStateListener!!)
	}

	override fun onStop() {
		super.onStop()
		mFirebaseAuth.removeAuthStateListener(mAuthStateListener!!)
	}



}
