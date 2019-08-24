package com.mmdev.data.core


import com.mmdev.data.messages.MessagesRepositoryImpl
import com.mmdev.data.user.AuthRepositoryImpl
import com.mmdev.domain.messages.repository.MessagesRepository
import com.mmdev.domain.user.repository.AuthRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun providesAuthRepository(repository: AuthRepositoryImpl): AuthRepository {
        return repository
    }

    @Provides
    fun providesMessagesRepository(repository: MessagesRepositoryImpl): MessagesRepository {
        return repository
    }
}