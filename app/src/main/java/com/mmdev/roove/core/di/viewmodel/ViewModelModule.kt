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

package com.mmdev.roove.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.cards.CardsViewModel
import com.mmdev.roove.ui.chat.ChatViewModel
import com.mmdev.roove.ui.conversations.ConversationsViewModel
import com.mmdev.roove.ui.pairs.PairsViewModel
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