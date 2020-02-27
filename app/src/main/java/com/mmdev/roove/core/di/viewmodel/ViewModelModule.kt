/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.02.20 16:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.auth.usecase.IsAuthenticatedListenerUseCase
import com.mmdev.business.auth.usecase.LogOutUseCase
import com.mmdev.business.auth.usecase.SignInUseCase
import com.mmdev.business.auth.usecase.SignUpUseCase
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.CheckMatchUseCase
import com.mmdev.business.cards.usecase.GetUsersByPreferencesUseCase
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.chat.usecase.LoadMessagesUseCase
import com.mmdev.business.chat.usecase.ObserveNewMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.UploadMessagePhotoUseCase
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.pairs.usecase.DeleteMatchUseCase
import com.mmdev.business.pairs.usecase.GetMatchedUsersUseCase
import com.mmdev.business.places.repository.PlacesRepository
import com.mmdev.business.places.usecase.GetPlaceDetailsUseCase
import com.mmdev.business.places.usecase.GetPlacesUseCase
import com.mmdev.business.remote.RemoteUserRepository
import com.mmdev.business.remote.usecase.*
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.core.viewmodel.RemoteUserRepoViewModel
import com.mmdev.roove.ui.core.viewmodel.SharedViewModel
import com.mmdev.roove.ui.dating.cards.CardsViewModel
import com.mmdev.roove.ui.dating.chat.ChatViewModel
import com.mmdev.roove.ui.dating.conversations.ConversationsViewModel
import com.mmdev.roove.ui.dating.pairs.PairsViewModel
import com.mmdev.roove.ui.places.PlacesViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider

/**
 * add new [ViewModel] down here
 */

@Module
class ViewModelModule {

	@Provides
	fun provideViewModelFactory(providers: MutableMap<Class<out ViewModel>,
			@JvmSuppressWildcards
			Provider<ViewModel>>): ViewModelProvider.Factory = ViewModelFactory(providers)


	@IntoMap
	@Provides
	@ViewModelKey(AuthViewModel::class)
	fun authViewModel(repo: AuthRepository): ViewModel =
		AuthViewModel(IsAuthenticatedListenerUseCase(repo),
		              LogOutUseCase(repo),
		              SignInUseCase(repo),
		              SignUpUseCase(repo))


	@IntoMap
	@Provides
	@ViewModelKey(CardsViewModel::class)
	fun cardsViewModel(repo: CardsRepository): ViewModel =
		CardsViewModel(AddToSkippedUseCase(repo),
		               CheckMatchUseCase(repo),
		               GetUsersByPreferencesUseCase(repo))

	@IntoMap
	@Provides
	@ViewModelKey(ChatViewModel::class)
	fun chatViewModel(repo: ChatRepository): ViewModel =
		ChatViewModel(LoadMessagesUseCase(repo),
		              ObserveNewMessagesUseCase(repo),
		              SendMessageUseCase(repo),
		              UploadMessagePhotoUseCase(repo))


	@IntoMap
	@Provides
	@ViewModelKey(ConversationsViewModel::class)
	fun conversationsViewModel(repo: ConversationsRepository): ViewModel =
		ConversationsViewModel(DeleteConversationUseCase(repo),
		                       GetConversationsListUseCase(repo))


//	@IntoMap
//	@Provides
//	@ViewModelKey(EventsViewModel::class)
//	fun eventsViewModel(repo: EventsRepository): ViewModel =
//		EventsViewModel(GetEventsUseCase(repo))


	@IntoMap
	@Provides
	@ViewModelKey(PairsViewModel::class)
	fun pairsViewModel(repo: PairsRepository): ViewModel =
		PairsViewModel(DeleteMatchUseCase(repo),
		               GetMatchedUsersUseCase(repo))


	@IntoMap
	@Provides
	@ViewModelKey(PlacesViewModel::class)
	fun placesViewModel(repo: PlacesRepository): ViewModel =
		PlacesViewModel(GetPlacesUseCase(repo),
		                GetPlaceDetailsUseCase(repo))

	@IntoMap
	@Provides
	@ViewModelKey(RemoteUserRepoViewModel::class)
	fun remoteUserRepoViewModel(repo: RemoteUserRepository): ViewModel =
		RemoteUserRepoViewModel(DeleteUserUseCase(repo),
		                        FetchUserInfoUseCase(repo),
		                        GetFullUserInfoUseCase(repo),
		                        UpdateUserItemUseCase(repo),
		                        UploadUserProfilePhotoUseCase(repo))

	@IntoMap
	@Provides
	@ViewModelKey(SharedViewModel::class)
	fun sharedViewModel(): ViewModel =
		SharedViewModel()

}