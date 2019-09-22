package com.mmdev.meetapp.core

import com.mmdev.data.core.AuthModule
import com.mmdev.data.core.DatabaseModule
import com.mmdev.data.core.RepositoryModule
import com.mmdev.data.core.UserModule
import com.mmdev.domain.auth.repository.AuthRepository
import com.mmdev.domain.cards.repository.CardsRepository
import com.mmdev.domain.messages.repository.ChatRepository
import com.mmdev.meetapp.ui.auth.viewmodel.AuthViewModelFactory
import com.mmdev.meetapp.ui.cards.viewmodel.CardsViewModelFactory
import com.mmdev.meetapp.ui.chat.viewmodel.ChatViewModelFactory
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


    //repos
    fun authRepository(): AuthRepository
    fun cardsRepository(): CardsRepository
    fun messagesRepository(): ChatRepository

}