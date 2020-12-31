/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import com.mmdev.business.auth.AuthRepository
import com.mmdev.business.cards.CardsRepository
import com.mmdev.business.chat.ChatRepository
import com.mmdev.business.conversations.ConversationsRepository
import com.mmdev.business.local.LocalUserRepository
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.remote.RemoteUserRepository
import com.mmdev.data.repository.auth.AuthRepositoryImpl
import com.mmdev.data.repository.cards.CardsRepositoryImpl
import com.mmdev.data.repository.chat.ChatRepositoryImpl
import com.mmdev.data.repository.conversations.ConversationsRepositoryImpl
import com.mmdev.data.repository.pairs.PairsRepositoryImpl
import com.mmdev.data.repository.user.UserRepositoryLocal
import com.mmdev.data.repository.user.UserRepositoryRemoteImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RepositoryModule {

	@Provides
	@Singleton
	fun authRepository(repository: AuthRepositoryImpl): AuthRepository = repository

	@Provides
	@Singleton
	fun cardsRepository(repository: CardsRepositoryImpl): CardsRepository = repository

	@Provides
	@Singleton
	fun chatRepository(repository: ChatRepositoryImpl): ChatRepository = repository

	@Provides
	@Singleton
	fun conversationsRepository(repository: ConversationsRepositoryImpl): ConversationsRepository = repository

	@Provides
	@Singleton
	fun pairsRepository(repository: PairsRepositoryImpl): PairsRepository = repository

	@Provides
	@Singleton
	fun localUserRepository(repository: UserRepositoryLocal): LocalUserRepository = repository

	@Provides
	@Singleton
	fun remoteUserRepository(repository: UserRepositoryRemoteImpl): RemoteUserRepository = repository

}
