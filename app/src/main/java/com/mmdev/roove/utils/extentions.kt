/*
 * Created by Andrii Kovalchuk on 01.12.19 22:42
 * Copyright (c) 2019. All rights reserved.
 * Last modified 01.12.19 21:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import android.graphics.Rect
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

fun AppCompatActivity.showToastText(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG)
	.show()

fun View.addSystemTopPadding(targetView: View = this, isConsumed: Boolean = false) {
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(
				top = initialPadding.top + insets.systemWindowInsetTop
		)
		if (isConsumed) {
			insets.replaceSystemWindowInsets(
					Rect(
							insets.systemWindowInsetLeft,
							0,
							insets.systemWindowInsetRight,
							insets.systemWindowInsetBottom
					)
			)
		} else {
			insets
		}
	}
}

fun View.addSystemBottomPadding(targetView: View = this, isConsumed: Boolean = false) {
	doOnApplyWindowInsets { _, insets, initialPadding ->
		targetView.updatePadding(
				bottom = initialPadding.bottom + insets.systemWindowInsetBottom
		)
		if (isConsumed) {
			insets.replaceSystemWindowInsets(
					Rect(
							insets.systemWindowInsetLeft,
							insets.systemWindowInsetTop,
							insets.systemWindowInsetRight,
							0
					)
			)
		} else {
			insets
		}
	}
}


fun View.doOnApplyWindowInsets(block: (View, insets: WindowInsetsCompat, initialPadding: Rect) -> WindowInsetsCompat) {
	val initialPadding = recordInitialPaddingForView(this)
	ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
		block(v, insets, initialPadding)
	}
	requestApplyInsetsWhenAttached()
}

private fun recordInitialPaddingForView(view: View) =
	Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

private fun View.requestApplyInsetsWhenAttached() {
	if (isAttachedToWindow) {
		ViewCompat.requestApplyInsets(this)
	}
	else {
		addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
			override fun onViewAttachedToWindow(v: View) {
				v.removeOnAttachStateChangeListener(this)
				ViewCompat.requestApplyInsets(v)
			}

			override fun onViewDetachedFromWindow(v: View) = Unit
		})
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