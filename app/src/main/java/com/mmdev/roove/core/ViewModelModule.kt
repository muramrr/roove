/*
 * Created by Andrii Kovalchuk on 20.11.19 21:38
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.11.19 21:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core


import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.auth.usecase.*
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.GetMatchedUsersUseCase
import com.mmdev.business.cards.usecase.GetPotentialUsersUseCase
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
import com.mmdev.business.user.repository.UserRepository
import com.mmdev.business.user.usecase.local.GetSavedUserUseCase
import com.mmdev.business.user.usecase.local.SaveUserInfoUseCase
import com.mmdev.business.user.usecase.remote.CreateUserUseCase
import com.mmdev.business.user.usecase.remote.DeleteUserUseCase
import com.mmdev.business.user.usecase.remote.GetUserByIdUseCase
import com.mmdev.roove.ui.actions.conversations.viewmodel.ConversationsViewModelFactory
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModelFactory
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModelFactory
import com.mmdev.roove.ui.chat.viewmodel.ChatViewModelFactory
import com.mmdev.roove.ui.events.viewmodel.EventsVMFactory
import com.mmdev.roove.ui.main.viewmodel.local.LocalUserRepoVMFactory
import com.mmdev.roove.ui.main.viewmodel.remote.RemoteUserRepoVMFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

	@Provides
	fun authViewModelFactory(repository: AuthRepository)=
		AuthViewModelFactory(HandleUserExistenceUseCase(repository),
		                     IsAuthenticatedUseCase(repository),
		                     LogOutUseCase(repository),
		                     SignInWithFacebookUseCase(repository),
		                     SignUpUseCase(repository))

	@Provides
	fun cardsViewModelFactory(repository: CardsRepository)=
		CardsViewModelFactory(AddToSkippedUseCase(repository),
		                      GetMatchedUsersUseCase(repository),
		                      GetPotentialUsersUseCase(repository),
		                      HandlePossibleMatchUseCase(repository))


	@Provides
	fun chatViewModelFactory(repository: ChatRepository)=
		ChatViewModelFactory(GetMessagesUseCase(repository),
		                     SendMessageUseCase(repository),
		                     SendPhotoUseCase(repository),
		                     SetConversationUseCase(repository))


	@Provides
	fun conversationsViewModelFactory(repository: ConversationsRepository)=
		ConversationsViewModelFactory(CreateConversationUseCase(repository),
		                              DeleteConversationUseCase(repository),
		                              GetConversationsListUseCase(repository))


	@Provides
	fun eventsRepoVMFactory(repository: EventsRepository) =
		EventsVMFactory(GetEventsUseCase(repository))


	@Provides
	fun localUserRepoVMFactory(repository: UserRepository.LocalUserRepository) =
		LocalUserRepoVMFactory(GetSavedUserUseCase(repository),
		                       SaveUserInfoUseCase(repository))



	@Provides
	fun remoteUserRepoVMFactory(repository: UserRepository.RemoteUserRepository) =
		RemoteUserRepoVMFactory(CreateUserUseCase(repository),
		                        DeleteUserUseCase(repository),
		                        GetUserByIdUseCase(repository))


}