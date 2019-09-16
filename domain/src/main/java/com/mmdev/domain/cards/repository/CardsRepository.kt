package com.mmdev.domain.cards.repository

import com.mmdev.domain.core.model.User

/* Created by A on 14.09.2019.*/

/**
 * This is the documentation block about the class
 */

interface CardsRepository {

	fun handlePossibleMatch(likedUser: User)

	fun addToSkipped(skipedUser: User)
}