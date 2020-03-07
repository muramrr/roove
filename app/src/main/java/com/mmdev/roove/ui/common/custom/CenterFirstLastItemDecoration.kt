/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 16:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.roove.R
import kotlin.math.round

/**
 * This is the documentation block about the class
 */

class CenterFirstLastItemDecoration: RecyclerView.ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
	                            state: RecyclerView.State) {
		val position = parent.getChildViewHolder(view).adapterPosition
		if (position == 0 || position == state.itemCount - 1) {
			val displayWidth = parent.context.resources.displayMetrics.widthPixels
			val childElementWidth = parent.context.resources.getDimension(R.dimen.rvSettingsPhotoElementWidth)
			//val elementMargin = 160
			val padding = round(displayWidth / 2f - childElementWidth / 2f).toInt()
			if (position == 0) { outRect.left = padding }
			else { outRect.right = padding }
		}
	}

}