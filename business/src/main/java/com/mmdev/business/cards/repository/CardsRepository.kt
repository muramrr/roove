package com.mmdev.business.cards.repository

import com.mmdev.business.user.model.UserItem
import io.reactivex.Single

/* Created by A on 14.09.2019.*/

/**
 * This is the documentation block about the class
 */

interface CardsRepository {

	fun addToSkipped(skippedUserItem: UserItem)

	fun getPotentialUserCards(): Single<List<UserItem>>

	fun handlePossibleMatch(likedUserItem: UserItem): Single<Boolean>


}