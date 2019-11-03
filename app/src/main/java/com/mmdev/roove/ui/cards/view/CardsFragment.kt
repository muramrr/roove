package com.mmdev.roove.ui.cards.view


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mmdev.business.cards.model.CardItem
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModel
import com.mmdev.roove.ui.main.view.MainActivity
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


class CardsFragment: Fragment(R.layout.fragment_card) {

	private lateinit var mMainActivity: MainActivity

	private lateinit var cardStackView: CardStackView
	private val mCardsStackAdapter: CardsStackAdapter = CardsStackAdapter(listOf())

	private lateinit var mLoadingImageView: ImageView
	private var mProgressShowing: Boolean = false

	private lateinit var mAppearedCardItem: CardItem

	private lateinit var cardsViewModel: CardsViewModel
	private val cardsViewModelFactory = injector.cardsViewModelFactory()

	private val disposables = CompositeDisposable()

	companion object{
		private const val TAG = "mylogs"

		fun newInstance(): CardsFragment {
			return CardsFragment()
		}
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }
		cardsViewModel = ViewModelProvider(mMainActivity, cardsViewModelFactory).get(CardsViewModel::class.java)

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		cardStackView = view.findViewById(R.id.card_stack_view)

		mCardsStackAdapter.setOnItemClickListener(object: CardsStackAdapter.OnItemClickListener{
			override fun onItemClick(view: View, position: Int) {
				Toast.makeText(mMainActivity,
				               "Clicked ${mCardsStackAdapter.getCard(position)}",
				               Toast.LENGTH_SHORT).show()
			}
		})

		mLoadingImageView = view.findViewById(R.id.card_loading_progress_iv)
		initLoadingGif()

		val textViewDescriptionHelper = view.findViewById<TextView>(R.id.card_helper_text_tv)
		//get potential users
		disposables.add(cardsViewModel.getPotentialUserCards()
			                .observeOn(AndroidSchedulers.mainThread())
			                .doOnSubscribe { showLoading() }
			                .doOnSuccess {
				                if(it.isNotEmpty()) hideLoading()
				                else textViewDescriptionHelper.visibility = View.VISIBLE
			                }
			                .subscribe({
				                           Log.wtf(TAG, "cards to show: ${it.size}")
				                           mCardsStackAdapter.updateData(it)

			                           },
			                           {
				                           Log.wtf(TAG, "get potential users error + $it")
			                           }))

		val cardStackLayoutManager = CardStackLayoutManager(mMainActivity, object: CardStackListener {

			override fun onCardAppeared(view: View, position: Int) {
				//get current displayed on card profile
				mAppearedCardItem = mCardsStackAdapter.getCard(position)

			}

			override fun onCardDragging(direction: Direction, ratio: Float) {}

			override fun onCardSwiped(direction: Direction) {
				val swipedCard = mAppearedCardItem
				//if right = add to liked
				//else = add to skiped
				if (direction == Direction.Right) {
					disposables.add(cardsViewModel.handlePossibleMatch(swipedCard)
						                .subscribe({
							                           if (it) showMatchDialog(swipedCard)
							                           Log.wtf(TAG, swipedCard.toString())
						                           },
						                           {
							                           Log.wtf(TAG, "error swiped + $it")
						                           }))
				}
				else cardsViewModel.addToSkipped(swipedCard)
			}

			override fun onCardRewound() {}

			override fun onCardCanceled() {}

			override fun onCardDisappeared(view: View, position: Int) {
				//if there is no available user to show - show loading
				if (position == mCardsStackAdapter.itemCount - 1) {
					mCardsStackAdapter.notifyDataSetChanged()
					showLoading()
					textViewDescriptionHelper.visibility = View.VISIBLE
				}
			}


		})

		cardStackView.apply {
			adapter = mCardsStackAdapter
			layoutManager = cardStackLayoutManager
		}

	}



	private fun showMatchDialog(matchCardItem: CardItem) {
		val matchDialog = Dialog(mMainActivity)
		matchDialog.setContentView(R.layout.dialog_match)
		//matchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//matchDialog.getWindow().setDimAmount(0.87f);
		matchDialog.show()
		matchDialog.window!!.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
		val backgr = matchDialog.findViewById<ImageView>(R.id.diag_match_iv_backgr_profile_img)
		GlideApp.with(this@CardsFragment)
			.load(matchCardItem.mainPhotoUrl)
			.centerInside()
			.into(backgr)
		matchDialog.findViewById<View>(R.id.diag_match_tv_keep_swp).setOnClickListener { matchDialog.dismiss() }
	}


	private fun initLoadingGif(){
		Glide.with(mLoadingImageView.context)
			.asGif()
			.load(R.drawable.loading)
			.centerCrop()
			.apply(RequestOptions().circleCrop())
			.into(mLoadingImageView)
	}

	private fun showLoading() {
		if (!mProgressShowing) {
			cardStackView.visibility = View.GONE
			mLoadingImageView.visibility = View.VISIBLE
			mProgressShowing = true
		}
	}

	private fun hideLoading() {
		if (mProgressShowing) {
			mLoadingImageView.visibility = View.GONE
			cardStackView.visibility = View.VISIBLE
			mProgressShowing = false
		}
	}

	override fun onResume() {
		super.onResume()
		mMainActivity.toolbar.title = "Cards"
	}

	override fun onDestroy(){
		super.onDestroy()
		disposables.clear()

	}
}
