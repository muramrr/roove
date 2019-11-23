/*
 * Created by Andrii Kovalchuk on 23.11.19 19:40
 * Copyright (c) 2019. All rights reserved.
 * Last modified 23.11.19 19:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.events.repository.EventsRepository
import com.mmdev.business.user.repository.UserRepository
import com.mmdev.roove.core.di.modules.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
	AuthModule::class,
	FirebaseModule::class,
	RepositoryModule::class,
	NetworkModule::class,
	ViewModelModule::class,
	UserModule::class
])
@Singleton
interface AppComponent {

	@Component.Builder
	interface Builder {

		@BindsInstance
		fun application(application: Application): Builder

		fun build(): AppComponent
	}

	fun factory(): ViewModelProvider.Factory

	//repos
	fun authRepository(): AuthRepository
	fun cardsRepository(): CardsRepository
	fun chatRepository(): ChatRepository
	fun conversationsRepository(): ConversationsRepository
	fun eventsRepository(): EventsRepository
	fun localUserRepository(): UserRepository.LocalUserRepository
	fun remoteUserRepository(): UserRepository.RemoteUserRepository

}