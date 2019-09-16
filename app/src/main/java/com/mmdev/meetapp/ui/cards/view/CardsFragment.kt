package com.mmdev.meetapp.ui.cards.view


import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mmdev.domain.core.model.User
import com.mmdev.meetapp.R
import com.mmdev.meetapp.ui.MainActivity
import com.mmdev.meetapp.ui.ProfileViewModel
import com.mmdev.meetapp.ui.cards.viewmodel.CardsViewModel
import com.mmdev.meetapp.utils.GlideApp
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction


class CardsFragment: Fragment(R.layout.fragment_card) {

	private lateinit var mMainActivity: MainActivity

	private lateinit var cardStackView: CardStackView
	private lateinit var mCardsStackAdapter: CardsStackAdapter
	private lateinit var mPotentialUsersList: MutableList<User>
	private lateinit var mSwipeUser: User
	private lateinit var progressBar: ProgressBar

	private var mProgressShowing: Boolean = false


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		if (activity != null) mMainActivity = activity as MainActivity
		cardStackView = view.findViewById(R.id.card_stack_view)
		progressBar = view.findViewById(R.id.card_prBar)

		val profileViewModel = ViewModelProvider(mMainActivity).get(ProfileViewModel::class.java)
		val profileModel = profileViewModel.getProfileModel(mMainActivity).value

		val cardsViewModel = ViewModelProvider(mMainActivity).get(CardsViewModel::class.java)


		mProgressShowing = false
		if (profileModel != null) {
			showLoadingBar()
			//get users from viewmodel
			cardsViewModel.init(profileModel)
			cardsViewModel.potentialUsersCards.observe(mMainActivity, Observer<List<User>>{ profileModelList ->
				mPotentialUsersList = profileModelList as MutableList<User>
				mCardsStackAdapter = CardsStackAdapter(mPotentialUsersList)
				cardStackView.adapter = mCardsStackAdapter
				if (mCardsStackAdapter.itemCount != 0) {
					hideLoadingBar()
					mCardsStackAdapter.notifyDataSetChanged()
				}
			})

			//handle match event

			cardsViewModel.matchedUser.observe(mMainActivity, Observer<User>{ matchedUser ->
				Toast.makeText(mMainActivity, "match!", Toast.LENGTH_SHORT).show()
				showMatchDialog(matchedUser)
			})
		}


		val cardStackLayoutManager = CardStackLayoutManager(mMainActivity, object: CardStackListener {

			override fun onCardAppeared(view: View, position: Int) {
				//get current displayed on card profile
				mSwipeUser = mCardsStackAdapter.getSwipeProfile(position)
			}

			override fun onCardDragging(direction: Direction, ratio: Float) {}

			override fun onCardSwiped(direction: Direction) {
				//if right = add to liked
				//else = add to skiped
				if (direction == Direction.Right) {
					cardsViewModel.handlePossibleMatch(mSwipeUser)
				}
				else cardsViewModel.addToSkipped(mSwipeUser)
				mCardsStackAdapter.notifyDataSetChanged()
				mPotentialUsersList.remove(mSwipeUser)
			}

			override fun onCardRewound() {}

			override fun onCardCanceled() {}

			override fun onCardDisappeared(view: View, position: Int) {
				//if there is no available user to show - show loading
				if (position == mCardsStackAdapter.itemCount - 1) {
					mCardsStackAdapter.notifyDataSetChanged()
					showLoadingBar()
				}
			}

		})

		cardStackView.layoutManager = cardStackLayoutManager

	}

	private fun showLoadingBar() {
		if (!mProgressShowing) {
			cardStackView.visibility = View.GONE
			progressBar.visibility = View.VISIBLE
			mProgressShowing = true
		}
	}

	private fun hideLoadingBar() {
		if (mProgressShowing) {
			progressBar.visibility = View.GONE
			cardStackView.visibility = View.VISIBLE
			mProgressShowing = false
		}
	}

	private fun showMatchDialog(matchUser: User) {
		val matchDialog = Dialog(mMainActivity)
		matchDialog.setContentView(R.layout.dialog_match)
		//matchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//matchDialog.getWindow().setDimAmount(0.87f);
		matchDialog.show()
		matchDialog.window!!.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
		val backgr = matchDialog.findViewById<ImageView>(R.id.diag_match_iv_backgr_profile_img)
		GlideApp.with(this).load(matchUser.mainPhotoUrl).centerInside().into(backgr)
		matchDialog.findViewById<View>(R.id.diag_match_tv_keep_swp).setOnClickListener { matchDialog.dismiss() }
	}


}
