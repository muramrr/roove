package com.mmdev.data.core


import com.mmdev.data.auth.AuthRepositoryImpl
import com.mmdev.data.cards.CardsRepositoryImpl
import com.mmdev.data.messages.ChatRepositoryImpl
import com.mmdev.domain.auth.repository.AuthRepository
import com.mmdev.domain.cards.repository.CardsRepository
import com.mmdev.domain.messages.repository.ChatRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun providesAuthRepository(repository: AuthRepositoryImpl): AuthRepository {
        return repository
    }

    @Provides
    fun providesChatRepository(repository: ChatRepositoryImpl): ChatRepository {
        return repository
    }

    @Provides
    fun providesCardsRepository(repository: CardsRepositoryImpl): CardsRepository{
        return repository
    }

}
