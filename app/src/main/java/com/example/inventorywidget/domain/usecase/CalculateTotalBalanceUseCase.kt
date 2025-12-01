package com.example.inventorywidget.domain.usecase



import com.example.inventorywidget.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalculateTotalBalanceUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): Double {
        val products = repository.getProductsSnapshot() // Get the list
        // Sum it up in Kotlin memory
        return products.sumOf { it.unitPrice * it.quantity }
    }
}