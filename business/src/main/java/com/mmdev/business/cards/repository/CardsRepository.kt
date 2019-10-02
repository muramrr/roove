package com.mmdev.business.cards.repository

import com.mmdev.business.user.model.User
import io.reactivex.Single

/* Created by A on 14.09.2019.*/

/**
 * This is the documentation block about the class
 */

interface CardsRepository {

	fun addToSkipped(skippedUser: User)

	fun getPotentialUserCards(): Single<List<User>>

	fun handlePossibleMatch(likedUser: User): Single<Boolean>


}