/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.02.20 18:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.custom.HorizontalCarouselLayoutManager
import com.mmdev.roove.ui.profile.view.PlacesToGoAdapter
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlin.math.round


/**
 * This is the documentation block about the class
 */

class SettingsFragment: BaseFragment(R.layout.fragment_settings) {

	private val mSettingsPhotoAdapter = SettingsUserPhotoAdapter(listOf())
	private val mPlacesToGoAdapter = PlacesToGoAdapter(listOf())

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var authViewModel: AuthViewModel

	private lateinit var userItem: UserItem

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.run {
			authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
			sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		sharedViewModel.getCurrentUser().observeOnce(this, Observer {
			userItem = it
			mSettingsPhotoAdapter.updateData(it.photoURLs)
			mPlacesToGoAdapter.updateData(it.placesToGo.toList())
			tvNameAge.text = it.baseUserInfo.name + ", " + it.baseUserInfo.age
		})
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		toolbarSettings.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.settings_action_log_out -> { showSignOutPrompt() }
			}
			return@setOnMenuItemClickListener true
		}

		rvUserPhotosList.apply {
			adapter = mSettingsPhotoAdapter
			layoutManager = HorizontalCarouselLayoutManager(this.context, HORIZONTAL, false)
			//item decorator to make first and last item align center
			addItemDecoration(object: RecyclerView.ItemDecoration(){
				override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
				                            state: RecyclerView.State) {
					val position = parent.getChildViewHolder(view).adapterPosition
					if (position == 0 || position == state.itemCount - 1) {
						val displayWidth = resources.displayMetrics.widthPixels
						val childElementWidth = resources.getDimension(R.dimen.rvSettingsPhotoElementWidth)
						//val elementMargin = 160
						val padding = round(displayWidth / 2f - childElementWidth / 2f).toInt()
						if (position == 0) { outRect.left = padding }
						else { outRect.right = padding }
					}
				}
			})
			val snapHelper: SnapHelper = LinearSnapHelper()
			snapHelper.attachToRecyclerView(this)
		}

		rvSettingsWantToGoList.apply { adapter = mPlacesToGoAdapter }

		mPlacesToGoAdapter.setOnItemClickListener(object: PlacesToGoAdapter.OnItemClickListener {

			override fun onItemClick(view: View, position: Int) {

				val placeId = bundleOf("PLACE_ID" to
						                       mPlacesToGoAdapter.getPlaceToGoItem(position).id)

				findNavController().navigate(R.id.action_settings_to_placeDetailedFragment, placeId)

			}

		})

		fabSettingsEdit.setOnClickListener {

			findNavController().navigate(R.id.action_settings_to_settingsEditInfoFragment)

		}
	}

	/*
	* log out pop up
	*/
	private fun showSignOutPrompt() {
		MaterialAlertDialogBuilder(context)
			.setTitle("Do you wish to log out?")
			.setMessage("This will permanently log you out.")
			.setPositiveButton("Log out") { dialog, _ ->
				authViewModel.logOut()
				dialog.dismiss()
			}
			.setNegativeButton("Cancel") { dialog, _ ->
				dialog.dismiss()
			}
			.show()

	}

}