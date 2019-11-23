/*
 * Created by Andrii Kovalchuk on 23.11.19 19:40
 * Copyright (c) 2019. All rights reserved.
 * Last modified 23.11.19 18:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.roove.core.di.viewmodel.ViewModelFactory
import com.mmdev.roove.core.di.viewmodel.ViewModelKey
import com.mmdev.roove.ui.actions.conversations.viewmodel.ConversationsViewModel
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModel
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModel
import com.mmdev.roove.ui.main.viewmodel.local.LocalUserRepoViewModel
import com.mmdev.roove.ui.main.viewmodel.remote.RemoteUserRepoViewModel
import com.mmdev.roove.ui.places.viewmodel.PlacesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * add new [ViewModel] down here
 */

@Module
abstract class ViewModelModule {

	@Binds
	internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

	@Binds
	@IntoMap
	@ViewModelKey(AuthViewModel::class)
	internal abstract fun authViewModel(viewModel: AuthViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(CardsViewModel::class)
	internal abstract fun cardsViewModel(viewModel: CardsViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(ConversationsViewModel::class)
	internal abstract fun conversationsViewModel(viewModel: ConversationsViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(PlacesViewModel::class)
	internal abstract fun placesViewModel(viewModel: PlacesViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(LocalUserRepoViewModel::class)
	internal abstract fun localUserRepoViewModel(viewModel: LocalUserRepoViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(RemoteUserRepoViewModel::class)
	internal abstract fun remoteUserRepoViewModel(viewModel: RemoteUserRepoViewModel): ViewModel

}