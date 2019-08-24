package com.mmdev.meetapp.core

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.data.core.DatabaseModule
import com.mmdev.data.core.RepositoryModule
import com.mmdev.domain.messages.repository.MessagesRepository
import com.mmdev.domain.user.repository.AuthRepository
import com.mmdev.meetapp.ui.chat.viewmodel.ChatViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    RepositoryModule::class,
    DatabaseModule::class,
    ViewModelModule::class
])
@Singleton
interface AppComponent {

    fun chatViewModelFactory(): ChatViewModelFactory

    fun messagesViewModelFactory(): MessagesViewModelFactory

    fun authRepository(): AuthRepository

    fun messagesRepository(): MessagesRepository

    fun firebaseFirestore(): FirebaseFirestore
}