package com.example.inventorywidget.domain.usecase

import com.example.inventorywidget.utils.Resource
import com.example.inventorywidget.model.User
import com.example.inventorywidget.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        return authRepository.register(email, password)
    }
}