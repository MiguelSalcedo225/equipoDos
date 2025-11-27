package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.inventorywidget.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * ViewModel para el Widget de Inventory
 * Maneja la lógica de cálculo del saldo total del inventario
 * siguiendo el patrón MVVM
 */
class WidgetViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val productRepository = ProductRepository(context)

    /**
     * Calcula el saldo total del inventario
     * Multiplica precio × cantidad de cada producto y suma todos
     * @return saldo total como Double
     */
    suspend fun calculateTotalBalance(): Double {
        return withContext(Dispatchers.IO) {
            try {
                val productList = productRepository.allProducts.first()
                var totalBalance = 0.0
                
                for (product in productList) {
                    val itemTotal = product.unitPrice * product.quantity
                    totalBalance += itemTotal
                }
                
                totalBalance
            } catch (e: Exception) {
                0.0
            }
        }
    }

    /**
     * Formatea el saldo con separadores de miles y dos decimales
     * Ejemplo: 1234567.89 -> $1,234,567.89
     * @param balance saldo a formatear
     * @return String formateado
     */
    fun formatBalance(balance: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        
        val formatter = DecimalFormat("#,##0.00", symbols)
        return "$${formatter.format(balance)}"
    }

    /**
     * Obtiene el saldo formateado oculto
     * @return String con formato oculto
     */
    fun getHiddenBalance(balance: Double): String {
        val formattedBalance = formatBalance(balance)
        val cleanBalance = formattedBalance.replace(Regex("[^0-9]"), "")
        val hiddenPart = "*".repeat(cleanBalance.length)
        return "$$hiddenPart"
    }
}
