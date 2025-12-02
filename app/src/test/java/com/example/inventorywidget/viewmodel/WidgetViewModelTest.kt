package com.example.inventorywidget.viewmodel

import android.app.Application
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class WidgetViewModelTest {

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockProductRepository: ProductRepository

    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth

    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    private lateinit var widgetViewModel: WidgetViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        // Configurar mock de Application
        `when`(mockApplication.applicationContext).thenReturn(mockApplication)
    }

    // ==================== Tests para isUserLoggedIn() ====================

    @Test
    fun `isUserLoggedIn returns true when user is authenticated`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.isUserLoggedIn()
        
        // Then
        assertTrue("Debe retornar true cuando el usuario está autenticado", result)
    }

    @Test
    fun `isUserLoggedIn returns false when user is not authenticated`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.isUserLoggedIn()
        
        // Then
        assertFalse("Debe retornar false cuando el usuario no está autenticado", result)
    }

    // ==================== Tests para calculateBalanceFromProducts() ====================

    @Test
    fun `calculateBalanceFromProducts returns zero for empty list`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        val emptyList = emptyList<Product>()
        
        // When
        val result = widgetViewModel.calculateBalanceFromProducts(emptyList)
        
        // Then
        assertEquals("El saldo debe ser 0 para lista vacía", 0.0, result, 0.001)
    }

    @Test
    fun `calculateBalanceFromProducts calculates correct total for single product`() {
        // Given - Criterio 8: precio × cantidad
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        val products = listOf(
            Product(code = 1, name = "Producto 1", unitPrice = 100.0, quantity = 5)
        )
        
        // When
        val result = widgetViewModel.calculateBalanceFromProducts(products)
        
        // Then
        assertEquals("100.0 * 5 = 500.0", 500.0, result, 0.001)
    }

    @Test
    fun `calculateBalanceFromProducts calculates correct total for multiple products`() {
        // Given - Criterio 8: suma de (precio × cantidad) de todos los productos
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        val products = listOf(
            Product(code = 1, name = "Producto 1", unitPrice = 100.0, quantity = 5),   // 500.0
            Product(code = 2, name = "Producto 2", unitPrice = 250.50, quantity = 10), // 2505.0
            Product(code = 3, name = "Producto 3", unitPrice = 75.25, quantity = 4)    // 301.0
        )
        // Total esperado: 500 + 2505 + 301 = 3306.0
        
        // When
        val result = widgetViewModel.calculateBalanceFromProducts(products)
        
        // Then
        assertEquals("Suma de todos los productos", 3306.0, result, 0.001)
    }

    @Test
    fun `calculateBalanceFromProducts handles decimal prices correctly`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        val products = listOf(
            Product(code = 1, name = "Producto 1", unitPrice = 99.99, quantity = 3)
        )
        
        // When
        val result = widgetViewModel.calculateBalanceFromProducts(products)
        
        // Then
        assertEquals("99.99 * 3 = 299.97", 299.97, result, 0.001)
    }

    @Test
    fun `calculateBalanceFromProducts handles zero quantity`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        val products = listOf(
            Product(code = 1, name = "Producto sin stock", unitPrice = 500.0, quantity = 0)
        )
        
        // When
        val result = widgetViewModel.calculateBalanceFromProducts(products)
        
        // Then
        assertEquals("Producto sin stock debe contribuir 0", 0.0, result, 0.001)
    }

    // ==================== Tests para formatBalance() ====================

    @Test
    fun `formatBalance formats number with correct separators`() {
        // Given - Criterio 9: separadores de miles y dos decimales
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.formatBalance(3326000.00)
        
        // Then - Ejemplo del criterio 9: 3.326.000,00
        assertEquals("Formato: $3.326.000,00", "$3.326.000,00", result)
    }

    @Test
    fun `formatBalance formats zero correctly`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.formatBalance(0.0)
        
        // Then
        assertEquals("Cero debe formatearse como $0,00", "$0,00", result)
    }

    @Test
    fun `formatBalance formats small numbers correctly`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.formatBalance(123.45)
        
        // Then
        assertEquals("Número pequeño formateado", "$123,45", result)
    }

    @Test
    fun `formatBalance formats large numbers with thousands separators`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.formatBalance(1234567.89)
        
        // Then
        assertEquals("Número grande con separadores", "$1.234.567,89", result)
    }

    @Test
    fun `formatBalance always shows two decimal places`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.formatBalance(1000.0)
        
        // Then
        assertTrue("Debe terminar con ,00", result.endsWith(",00"))
    }

    // ==================== Tests para getHiddenBalance() ====================

    @Test
    fun `getHiddenBalance returns correct format`() {
        // Given - Criterio 5: signo de pesos y 4 asteriscos
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.getHiddenBalance()
        
        // Then
        assertEquals("Formato oculto debe ser $****", "$****", result)
    }

    @Test
    fun `getHiddenBalance starts with dollar sign`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.getHiddenBalance()
        
        // Then
        assertTrue("Debe empezar con $", result.startsWith("$"))
    }

    @Test
    fun `getHiddenBalance contains asterisks`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.getHiddenBalance()
        
        // Then
        assertTrue("Debe contener asteriscos", result.contains("*"))
    }

    // ==================== Tests para isValidBalanceFormat() ====================

    @Test
    fun `isValidBalanceFormat returns true for valid format`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.isValidBalanceFormat("$1.234.567,89")
        
        // Then
        assertTrue("Formato válido debe retornar true", result)
    }

    @Test
    fun `isValidBalanceFormat returns true for zero amount`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.isValidBalanceFormat("$0,00")
        
        // Then
        assertTrue("Cero formateado debe ser válido", result)
    }

    @Test
    fun `isValidBalanceFormat returns false for invalid format without dollar sign`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.isValidBalanceFormat("1.234.567,89")
        
        // Then
        assertFalse("Sin símbolo $ debe ser inválido", result)
    }

    @Test
    fun `isValidBalanceFormat returns false for hidden balance`() {
        // Given
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        // When
        val result = widgetViewModel.isValidBalanceFormat("$****")
        
        // Then
        assertFalse("Balance oculto no es un formato de saldo válido", result)
    }

    // ==================== Tests de integración ====================

    @Test
    fun `complete flow - calculate and format balance`() {
        // Given - Simular flujo completo
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        widgetViewModel = WidgetViewModel(mockApplication, mockProductRepository, mockFirebaseAuth)
        
        val products = listOf(
            Product(code = 1, name = "Laptop", unitPrice = 1500000.0, quantity = 2),   // 3,000,000
            Product(code = 2, name = "Mouse", unitPrice = 50000.0, quantity = 10),      // 500,000
            Product(code = 3, name = "Teclado", unitPrice = 80000.0, quantity = 5)      // 400,000
        )
        // Total: 3,900,000
        
        // When
        val balance = widgetViewModel.calculateBalanceFromProducts(products)
        val formatted = widgetViewModel.formatBalance(balance)
        val isLoggedIn = widgetViewModel.isUserLoggedIn()
        
        // Then
        assertEquals("Balance calculado correctamente", 3900000.0, balance, 0.001)
        assertEquals("Balance formateado correctamente", "$3.900.000,00", formatted)
        assertTrue("Usuario debe estar logueado", isLoggedIn)
        assertTrue("Formato debe ser válido", widgetViewModel.isValidBalanceFormat(formatted))
    }
}
