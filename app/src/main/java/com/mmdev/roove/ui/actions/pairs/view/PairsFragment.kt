package com.mmdev.roove.ui.actions.pairs.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModel
import com.mmdev.roove.ui.main.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/* Created by A on 13.11.2019.*/

/**
 * This is the documentation block about the class
 */

class PairsFragment: Fragment(R.layout.fragment_pairs) {


	private lateinit var mMainActivity: MainActivity

	private val mPairsAdapter: PairsAdapter = PairsAdapter(listOf())

	//for potential
	private lateinit var cardsViewModel: CardsViewModel
	private val cardsViewModelFactory = injector.cardsViewModelFactory()

	private val disposables = CompositeDisposable()

	companion object {
		private const val TAG = "mylogs"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }

		cardsViewModel = ViewModelProvider(mMainActivity, cardsViewModelFactory).get(CardsViewModel::class.java)

		//get matched users
		disposables.add(cardsViewModel.getMatchedUserItems()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           Log.wtf(TAG, "pairs to show: ${it.size}")
                           mPairsAdapter.updateData(it)
                       },
                       {
                           Log.wtf(TAG, "error + $it")
                       }))
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val rvPairsList = view.findViewById<RecyclerView>(R.id.pairs_container_rv)
		rvPairsList.apply {
			adapter = mPairsAdapter
			layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
			itemAnimator = DefaultItemAnimator()
		}

		mPairsAdapter.setOnItemClickListener(object: PairsAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {
				val pairItem = mPairsAdapter.getPairItem(position)

				mMainActivity.partnerName = pairItem.name
				mMainActivity.cardItemClicked = pairItem

				mMainActivity.startProfileFragment(pairItem.userId, true)


			}
		})
	}



	override fun onDestroy() {
		super.onDestroy()
		disposables.clear()
	}


}