package com.mmdev.meetapp.core


import com.mmdev.data.messages.MessagesRepositoryImpl
import com.mmdev.data.user.AuthRepositoryImpl
import com.mmdev.domain.messages.usecase.GetMessagesUseCase
import com.mmdev.domain.messages.usecase.SendMessageUseCase
import com.mmdev.domain.user.usecase.LoginUseCase
import com.mmdev.domain.user.usecase.SignUpUseCase
import com.mmdev.meetapp.ui.chat.viewmodel.ChatViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    fun providesAuthViewModelFactory(repository: AuthRepositoryImpl): ChatViewModelFactory {
        return ChatViewModelFactory(
                SignUpUseCase(repository),
                LoginUseCase(repository)
        )
    }

    @Provides
    fun providesMessagesViewModelFactory(repository: MessagesRepositoryImpl): MessagesViewModelFactory {
        return MessagesViewModelFactory(
                GetMessagesUseCase(repository),
                SendMessageUseCase(repository)
        )
    }
}