package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * ViewModel para el Widget de Inventory
 * Maneja la lógica de cálculo del saldo total del inventario
 * siguiendo el patrón MVVM
 */
class WidgetViewModel(
    application: Application,
    private val productRepository: ProductRepository? = null,
    private val firebaseAuth: FirebaseAuth? = null
) : AndroidViewModel(application) {

    private val repository: ProductRepository = productRepository 
        ?: ProductRepository(FirebaseFirestore.getInstance())
    private val auth: FirebaseAuth = firebaseAuth ?: FirebaseAuth.getInstance()

    /**
     * Verifica si el usuario está logueado
     * @return true si el usuario está autenticado
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Calcula el saldo total del inventario
     * Criterio 8: Multiplica precio × cantidad de cada producto y suma todos
     * Usa getProductsSnapshot() para obtener datos frescos directamente de Firestore
     * @return saldo total como Double
     */
    suspend fun calculateTotalBalance(): Double {
        return withContext(Dispatchers.IO) {
            try {
                // Usar getProductsSnapshot() para obtener datos frescos de Firestore
                // en lugar de allProducts().first() que usa el listener cache
                val productList = repository.getProductsSnapshot()
                calculateBalanceFromProducts(productList)
            } catch (e: Exception) {
                0.0
            }
        }
    }

    /**
     * Calcula el saldo total a partir de una lista de productos
     * @param products lista de productos
     * @return suma de (precio unitario × cantidad) de todos los productos
     */
    fun calculateBalanceFromProducts(products: List<Product>): Double {
        var totalBalance = 0.0
        for (product in products) {
            val itemTotal = product.unitPrice * product.quantity
            totalBalance += itemTotal
        }
        return totalBalance
    }

    /**
     * Formatea el saldo con separadores de miles y dos decimales
     * Criterio 9: Ejemplo 3.326.000,00
     * @param balance saldo a formatear
     * @return String formateado con símbolo de pesos
     */
    fun formatBalance(balance: Double): String {
        val symbols = DecimalFormatSymbols(Locale.GERMANY).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        
        val formatter = DecimalFormat("#,##0.00", symbols)
        return "$${formatter.format(balance)}"
    }

    /**
     * Obtiene el saldo formateado oculto
     * Criterio 5: Signo de pesos y 4 asteriscos
     * @return String con formato oculto ($****)
     */
    fun getHiddenBalance(): String {
        return "$****"
    }

    /**
     * Valida el formato del saldo
     * @param formattedBalance saldo formateado
     * @return true si cumple con el formato esperado
     */
    fun isValidBalanceFormat(formattedBalance: String): Boolean {
        // Debe empezar con $ y contener números con separadores
        val pattern = Regex("^\\$[0-9.]+,[0-9]{2}$")
        return pattern.matches(formattedBalance)
    }
}
