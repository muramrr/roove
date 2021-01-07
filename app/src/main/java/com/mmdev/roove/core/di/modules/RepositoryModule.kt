/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

import com.google.firebase.auth.FirebaseAuth
import com.mmdev.data.datasource.auth.AuthCollector
import com.mmdev.data.repository.auth.AuthRepositoryImpl
import com.mmdev.data.repository.cards.CardsRepositoryImpl
import com.mmdev.data.repository.chat.ChatRepositoryImpl
import com.mmdev.data.repository.conversations.ConversationsRepositoryImpl
import com.mmdev.data.repository.pairs.PairsRepositoryImpl
import com.mmdev.data.repository.user.SettingsRepositoryImpl
import com.mmdev.data.repository.user.UserRepositoryImpl
import com.mmdev.domain.auth.AuthRepository
import com.mmdev.domain.cards.CardsRepository
import com.mmdev.domain.chat.ChatRepository
import com.mmdev.domain.conversations.ConversationsRepository
import com.mmdev.domain.pairs.PairsRepository
import com.mmdev.domain.user.ISettingsRepository
import com.mmdev.domain.user.IUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
	
	@Provides
	@Singleton
	fun authCollector(auth: FirebaseAuth): AuthCollector = AuthCollector(auth)
	
	@Provides
	fun authRepository(repository: AuthRepositoryImpl): AuthRepository = repository

	@Provides
	fun cardsRepository(repository: CardsRepositoryImpl): CardsRepository = repository

	@Provides
	fun chatRepository(repository: ChatRepositoryImpl): ChatRepository = repository

	@Provides
	fun conversationsRepository(repository: ConversationsRepositoryImpl): ConversationsRepository = repository

	@Provides
	fun pairsRepository(repository: PairsRepositoryImpl): PairsRepository = repository

	@Provides
	fun userRepository(repository: UserRepositoryImpl): IUserRepository = repository
	
	@Provides
	fun userSettingsRepository(repository: SettingsRepositoryImpl): ISettingsRepository = repository

}
