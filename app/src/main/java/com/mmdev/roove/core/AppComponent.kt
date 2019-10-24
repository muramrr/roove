package com.mmdev.roove.core

import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.user.repository.UserRepository
import com.mmdev.data.core.AuthModule
import com.mmdev.data.core.DatabaseModule
import com.mmdev.data.core.RepositoryModule
import com.mmdev.data.core.UserModule
import com.mmdev.roove.ui.auth.viewmodel.AuthViewModelFactory
import com.mmdev.roove.ui.cards.viewmodel.CardsViewModelFactory
import com.mmdev.roove.ui.chat.viewmodel.ChatViewModelFactory
import com.mmdev.roove.ui.main.viewmodel.MainViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    AuthModule::class,
    RepositoryModule::class,
    DatabaseModule::class,
    ViewModelModule::class,
    UserModule::class
])

@Singleton
interface AppComponent {

    //factories
    fun authViewModelFactory(): AuthViewModelFactory
    fun cardsViewModelFactory(): CardsViewModelFactory
    fun chatViewModelFactory(): ChatViewModelFactory
    fun mainViewModelFactory(): MainViewModelFactory


    //repos
    fun authRepository(): AuthRepository
    fun cardsRepository(): CardsRepository
    fun messagesRepository(): ChatRepository
    fun userRepository(): UserRepository

}