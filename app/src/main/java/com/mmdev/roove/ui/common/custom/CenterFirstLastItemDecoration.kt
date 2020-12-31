/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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