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
import android.widget.Toast
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
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.meetapp.R
import com.mmdev.meetapp.models.ProfileModel
import com.mmdev.meetapp.ui.auth.AuthActivity
import com.mmdev.meetapp.ui.card.CardFragment
import com.mmdev.meetapp.ui.chat.view.ChatFragment
import com.mmdev.meetapp.ui.feed.FeedFragment
import com.mmdev.meetapp.ui.feed.FeedManager
import com.mmdev.meetapp.utils.GlideApp
import java.util.*

class MainActivity: AppCompatActivity(), MainActivityListeners  {

	private val TAG = "myLogs"

	// Data
	private var usersCards: MutableList<ProfileModel> = ArrayList()

	private lateinit var navView: NavigationView
	private var toolbar: Toolbar? = null

	private var drawerLayout: DrawerLayout? = null

	private var tvSignedInUserName: TextView? = null
	private var tvSignedInUserId: TextView? = null
	private var ivSignedInUserAvatar: ImageView? = null


	// Firebase
	private var mFirebaseAuth: FirebaseAuth? = null
	private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
	private var mFirestore: FirebaseFirestore? = null
	var profileModel: ProfileModel? = null
	private var mFragmentManager: FragmentManager? = null


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setUpViews()
		setSupportActionBar(toolbar)
		setUpNavigationView(navView, drawerLayout!!)
		showFeedFragment()
		mFirebaseAuth = FirebaseAuth.getInstance()
		checkConnection()
		mFragmentManager = supportFragmentManager

		mFirestore = FirebaseFirestore.getInstance()
		val profileViewModel =
			ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)
		profileModel = profileViewModel.getProfileModel(this).value
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
		supportFragmentManager.findFragmentByTag("CardFragment") ?: supportFragmentManager
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
		supportFragmentManager.findFragmentByTag("ChatFragment") ?: supportFragmentManager
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
			mFirebaseAuth!!.signOut()
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
		if (profileModel != null) {
			val mName = profileModel!!.name
			val mCity = profileModel!!.city
			val mGender = profileModel!!.gender
			val mPreferedGender = profileModel!!.preferedGender
			val signedInUserPhotoUrl = profileModel!!.mainPhotoUrl
			val uID = profileModel!!.userId
			if (!TextUtils.isEmpty(signedInUserPhotoUrl))
				GlideApp.with(this).load(signedInUserPhotoUrl).into(ivSignedInUserAvatar!!)
			tvSignedInUserName!!.text = mName
			tvSignedInUserId!!.text = uID
		}
		else Toast.makeText(this, "No user login info", Toast.LENGTH_LONG).show()

	}

	private fun setUpViews(){
		navView = findViewById(R.id.nav_view)
		drawerLayout = findViewById(R.id.drawer_layout)
		toolbar = findViewById(R.id.toolbar)
		val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
		                                   R.string.navigation_drawer_open,
		                                   R.string.navigation_drawer_close)

		drawerLayout!!.addDrawerListener(toggle)
		toggle.syncState()
	}

	private fun setUpNavigationView(navView: NavigationView, drawerLayout: DrawerLayout) {
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
				R.id.nav_post -> onGenerateUsers()
				R.id.nav_notifications -> { }
				R.id.nav_account -> { }
				R.id.nav_log_out -> onLogOutClick()
			} //Toast.makeText(this,String.valueOf(mFeedManager.getUsersCards()),Toast.LENGTH_SHORT).show();
			//Toast.makeText(this, String.valueOf(FeedManager.generateUsers()), Toast.LENGTH_SHORT).show();
			return@setNavigationItemSelectedListener true
		}

	}

	private fun showFeedFragment(){
		supportFragmentManager.beginTransaction().apply {
			add(R.id.main_container, FeedFragment(), "FeedFragment")
			commit()
		}
	}

	/*
	 * generate random users to firestore
	 */
	private fun onGenerateUsers() {
		usersCards.clear()
		val usersCollection = mFirestore!!.collection("users")
		usersCards.addAll(FeedManager.generateUsers())
		for (i in usersCards) usersCollection.document(i.userId).set(i)


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
		if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) drawerLayout!!.closeDrawer(GravityCompat.START)
		else super.onBackPressed()
	}

//	override fun onStart() {
//		super.onStart()
//		mFirebaseAuth!!.addAuthStateListener(mAuthStateListener!!)
//	}
//
//	override fun onStop() {
//		super.onStop()
//		mFirebaseAuth!!.removeAuthStateListener(mAuthStateListener!!)
//	}



}
