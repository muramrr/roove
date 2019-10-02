package com.mmdev.data.core


import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.user.repository.UserRepository
import com.mmdev.data.auth.AuthRepositoryImpl
import com.mmdev.data.cards.CardsRepositoryImpl
import com.mmdev.data.chat.ChatRepositoryImpl
import com.mmdev.data.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

	@Provides
	fun providesAuthRepository(repository: AuthRepositoryImpl): AuthRepository { return repository }

	@Provides
	fun providesChatRepository(repository: ChatRepositoryImpl): ChatRepository { return repository }

	@Provides
	fun providesCardsRepository(repository: CardsRepositoryImpl): CardsRepository{ return repository }

	@Provides
	fun providesUserRepository(repository: UserRepositoryImpl): UserRepository { return repository }

}
