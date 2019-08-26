package com.mmdev.meetapp.core

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.data.core.AuthModule
import com.mmdev.data.core.DatabaseModule
import com.mmdev.data.core.RepositoryModule
import com.mmdev.domain.messages.repository.ChatRepository
import com.mmdev.domain.user.repository.AuthRepository
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

   // fun chatViewModelFactory(): ChatViewModelFactory

    fun chatViewModelFactory(): ChatViewModelFactory

    fun authRepository(): AuthRepository

    fun messagesRepository(): ChatRepository

    fun firebaseFirestore(): FirebaseFirestore
}