/*
 * Created by Andrii Kovalchuk on 26.11.19 20:29
 * Copyright (c) 2019. All rights reserved.
 * Last modified 26.11.19 18:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.auth.usecase.*
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.GetUsersByPreferencesUseCase
import com.mmdev.business.cards.usecase.HandlePossibleMatchUseCase
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.chat.usecase.GetMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.SendPhotoUseCase
import com.mmdev.business.chat.usecase.SetConversationUseCase
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.conversations.usecase.CreateConversationUseCase
import com.mmdev.business.conversations.usecase.DeleteConversationUseCase
import com.mmdev.business.conversations.usecase.GetConversationsListUseCase
import com.mmdev.business.events.repository.EventsRepository
import com.mmdev.business.events.usecase.GetEventsUseCase
import com.mmdev.business.pairs.GetMatchedUsersUseCase
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.user.repository.UserRepository
import com.mmdev.business.user.usecase.local.GetSavedUserUseCase
import com.mmdev.business.user.usecase.local.SaveUserInfoUseCase
import com.mmdev.business.user.usecase.remote.CreateUserUseCase
import com.mmdev.business.user.usecase.remote.DeleteUserUseCase
import com.mmdev.business.user.usecase.remote.GetUserByIdUseCase
import com.mmdev.roove.core.di.viewmodel.ViewModelFactory
import com.mmdev.roove.core.di.viewmodel.ViewModelKey
import com.mmdev.roove.ui.actions.conversations.ConversationsViewModel
import com.mmdev.roove.ui.actions.pairs.PairsViewModel
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModel
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModel
import com.mmdev.roove.ui.chat.viewmodel.ChatViewModel
import com.mmdev.roove.ui.main.viewmodel.local.LocalUserRepoViewModel
import com.mmdev.roove.ui.main.viewmodel.remote.RemoteUserRepoViewModel
import com.mmdev.roove.ui.places.viewmodel.PlacesViewModel
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
			@JvmSuppressWildcards Provider<ViewModel>>): ViewModelProvider.Factory =
		ViewModelFactory(providers)


	@IntoMap
	@Provides
	@ViewModelKey(AuthViewModel::class)
	fun authViewModel(repository: AuthRepository): ViewModel =
		AuthViewModel(HandleUserExistenceUseCase(repository),
			                IsAuthenticatedUseCase(repository),
			                LogOutUseCase(repository),
			                SignInWithFacebookUseCase(repository),
			                SignUpUseCase(repository))


	@IntoMap
	@Provides
	@ViewModelKey(CardsViewModel::class)
	fun cardsViewModel(repository: CardsRepository): ViewModel =
		CardsViewModel(AddToSkippedUseCase(repository),
		               GetUsersByPreferencesUseCase(repository),
		               HandlePossibleMatchUseCase(repository))

	@IntoMap
	@Provides
	@ViewModelKey(ChatViewModel::class)
	fun chatViewModel(repository: ChatRepository): ViewModel =
		ChatViewModel(GetMessagesUseCase(repository),
		              SendMessageUseCase(repository),
		              SendPhotoUseCase(repository),
		              SetConversationUseCase(repository))


	@IntoMap
	@Provides
	@ViewModelKey(ConversationsViewModel::class)
	fun conversationsViewModel(repository: ConversationsRepository): ViewModel =
		ConversationsViewModel(
				CreateConversationUseCase(repository),
				DeleteConversationUseCase(repository),
				GetConversationsListUseCase(repository))


	@IntoMap
	@Provides
	@ViewModelKey(PairsViewModel::class)
	fun pairsViewModel(repository: PairsRepository): ViewModel =
		PairsViewModel(GetMatchedUsersUseCase(repository))


	@IntoMap
	@Provides
	@ViewModelKey(PlacesViewModel::class)
	fun placesViewModel(repository: EventsRepository): ViewModel =
		PlacesViewModel(GetEventsUseCase(repository))


	@IntoMap
	@Provides
	@ViewModelKey(LocalUserRepoViewModel::class)
	fun localUserRepoViewModel(repository: UserRepository.LocalUserRepository): ViewModel =
		LocalUserRepoViewModel(GetSavedUserUseCase(repository),
		                       SaveUserInfoUseCase(repository))


	@IntoMap
	@Provides
	@ViewModelKey(RemoteUserRepoViewModel::class)
	fun remoteUserRepoViewModel(repository: UserRepository.RemoteUserRepository): ViewModel =
		RemoteUserRepoViewModel(CreateUserUseCase(repository),
		                        DeleteUserUseCase(repository),
		                        GetUserByIdUseCase(repository))

}