package com.mmdev.roove.ui.main.viewmodel.remote

import androidx.lifecycle.ViewModel
import com.mmdev.business.user.usecase.remote.CreateUserUseCase
import com.mmdev.business.user.usecase.remote.DeleteUserUseCase
import com.mmdev.business.user.usecase.remote.GetUserByIdUseCase

/* Created by A on 10.11.2019.*/

/**
 * This is the documentation block about the class
 */

class RemoteUserRepoVM (private val createUserUC: CreateUserUseCase,
                        private val deleteUserUC: DeleteUserUseCase,
                        private val getUserUC: GetUserByIdUseCase) : ViewModel() {

	fun createUser() = createUserUC.execute()

	fun deleteUser(userId: String) = deleteUserUC.execute(userId)

	fun getUserById(userId: String) = getUserUC.execute(userId)


}