package com.example.inventorywidget.domain.usecase



import com.example.inventorywidget.repository.AuthRepository
import javax.inject.Inject

class VerifyUserIsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.currentUser != null
    }
}