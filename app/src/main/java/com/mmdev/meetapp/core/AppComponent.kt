package com.mmdev.meetapp.core

import com.mmdev.data.core.AuthModule
import com.mmdev.data.core.DatabaseModule
import com.mmdev.data.core.RepositoryModule
import com.mmdev.domain.auth.repository.AuthRepository
import com.mmdev.domain.messages.repository.ChatRepository
import com.mmdev.meetapp.ui.auth.viewmodel.AuthViewModelFactory
import com.mmdev.meetapp.ui.chat.viewmodel.ChatViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    AuthModule::class,
    RepositoryModule::class,
    DatabaseModule::class,
    ViewModelModule::class
])
@Singleton
interface AppComponent {

    fun chatViewModelFactory(): ChatViewModelFactory

    fun authViewModelFactory(): AuthViewModelFactory

    fun authRepository(): AuthRepository

    fun messagesRepository(): ChatRepository
}