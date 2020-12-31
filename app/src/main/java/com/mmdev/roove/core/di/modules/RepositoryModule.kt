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
import javax.inject.Singleton

@Module
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
