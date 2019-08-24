package com.mmdev.data.core


import com.mmdev.data.messages.ChatRepositoryImpl
import com.mmdev.data.user.AuthRepositoryImpl
import com.mmdev.domain.messages.repository.ChatRepository
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
    fun providesMessagesRepository(repository: ChatRepositoryImpl): ChatRepository {
        return repository
    }
}