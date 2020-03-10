/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.03.20 20:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.dating.cards.CardsViewModel
import com.mmdev.roove.ui.dating.chat.ChatViewModel
import com.mmdev.roove.ui.dating.conversations.ConversationsViewModel
import com.mmdev.roove.ui.dating.pairs.PairsViewModel
import com.mmdev.roove.ui.places.PlacesViewModel
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * add new [ViewModel] down here
 */

@Module
abstract class ViewModelModule {


	@Binds
	abstract fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory


	@IntoMap
	@Binds
	@ViewModelKey(AuthViewModel::class)
	abstract fun authViewModel(authViewModel: AuthViewModel): ViewModel


	@IntoMap
	@Binds
	@ViewModelKey(CardsViewModel::class)
	abstract fun cardsViewModel(cardsViewModel: CardsViewModel): ViewModel

	@IntoMap
	@Binds
	@ViewModelKey(ChatViewModel::class)
	abstract fun chatViewModel(chatViewModel: ChatViewModel): ViewModel


	@IntoMap
	@Binds
	@ViewModelKey(ConversationsViewModel::class)
	abstract fun conversationsViewModel(conversationsViewModel: ConversationsViewModel): ViewModel


	@IntoMap
	@Binds
	@ViewModelKey(PairsViewModel::class)
	abstract fun pairsViewModel(pairsViewModel: PairsViewModel): ViewModel


	@IntoMap
	@Binds
	@ViewModelKey(PlacesViewModel::class)
	abstract fun placesViewModel(placesViewModel: PlacesViewModel): ViewModel

	@IntoMap
	@Binds
	@ViewModelKey(RemoteRepoViewModel::class)
	abstract fun remoteUserRepoViewModel(remoteRepoViewModel: RemoteRepoViewModel): ViewModel

	@IntoMap
	@Binds
	@ViewModelKey(SharedViewModel::class)
	abstract fun sharedViewModel(sharedViewModel: SharedViewModel): ViewModel

}