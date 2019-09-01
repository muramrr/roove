package com.mmdev.meetapp.core


import com.mmdev.data.auth.AuthRepositoryImpl
import com.mmdev.data.messages.ChatRepositoryImpl
import com.mmdev.domain.auth.usecase.*
import com.mmdev.domain.messages.usecase.GetMessagesUseCase
import com.mmdev.domain.messages.usecase.SendMessageUseCase
import com.mmdev.domain.messages.usecase.SendPhotoUseCase
import com.mmdev.meetapp.ui.auth.viewmodel.AuthViewModelFactory
import com.mmdev.meetapp.ui.chat.viewmodel.ChatViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

	@Provides
	fun providesAuthViewModelFactory(repository: AuthRepositoryImpl): AuthViewModelFactory{
		return AuthViewModelFactory(HandleUserExistenceUseCase(repository),
		                            IsAuthenticatedUseCase(repository),
		                            LogOutUseCase(repository),
		                            SignInWithFacebookUseCase(repository),
		                            SignUpUseCase(repository))
	}

	@Provides
	fun providesChatViewModelFactory(repository: ChatRepositoryImpl): ChatViewModelFactory {
		return ChatViewModelFactory(GetMessagesUseCase(repository), SendMessageUseCase(repository),
		                            SendPhotoUseCase(repository))
	}
}