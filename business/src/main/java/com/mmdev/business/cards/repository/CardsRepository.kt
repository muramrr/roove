package com.mmdev.business.cards.repository

import com.mmdev.business.cards.model.CardItem
import io.reactivex.Observable
import io.reactivex.Single

/* Created by A on 14.09.2019.*/

/**
 * This is the documentation block about the class
 */

interface CardsRepository {

	fun addToSkipped(skippedCardItem: CardItem)

	fun getMatchedCardItems(): Observable<List<CardItem>>

	fun getPotentialCardItems(): Single<List<CardItem>>

	fun handlePossibleMatch(likedCardItem: CardItem): Single<Boolean>


}
