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

import android.app.Application
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.events.repository.EventsRepository
import com.mmdev.business.user.repository.UserRepository
import com.mmdev.roove.core.di.*
import com.mmdev.roove.ui.actions.conversations.viewmodel.ConversationsViewModelFactory
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModelFactory
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModelFactory
import com.mmdev.roove.ui.chat.viewmodel.ChatViewModelFactory
import com.mmdev.roove.ui.events.viewmodel.EventsVMFactory
import com.mmdev.roove.ui.main.viewmodel.local.LocalUserRepoVMFactory
import com.mmdev.roove.ui.main.viewmodel.remote.RemoteUserRepoVMFactory
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

		fun build(): AppComponent

		@BindsInstance
		fun application(application: Application): Builder

	}

	//factories
	fun authViewModelFactory(): AuthViewModelFactory
	fun cardsViewModelFactory(): CardsViewModelFactory
	fun chatViewModelFactory(): ChatViewModelFactory
	fun conversationsViewModelFactory(): ConversationsViewModelFactory
	fun eventsVMFactory(): EventsVMFactory
	fun localUserRepoVMFactory(): LocalUserRepoVMFactory
	fun remoteUserRepoVMFactory(): RemoteUserRepoVMFactory


	//repos
	fun authRepository(): AuthRepository
	fun cardsRepository(): CardsRepository
	fun chatRepository(): ChatRepository
	fun conversationsRepository(): ConversationsRepository
	fun eventsRepository(): EventsRepository
	fun localUserRepository(): UserRepository.LocalUserRepository
	fun remoteUserRepository(): UserRepository.RemoteUserRepository

}