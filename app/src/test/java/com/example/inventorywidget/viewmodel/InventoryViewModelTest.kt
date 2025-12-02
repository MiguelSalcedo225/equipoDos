package com.example.inventorywidget.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inventorywidget.domain.usecase.CalculateTotalBalanceUseCase
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class InventoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: ProductRepository

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var calculateTotalBalanceUseCase: CalculateTotalBalanceUseCase

    private lateinit var viewModel: InventoryViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        `when`(repository.allProducts()).thenReturn(flowOf(emptyList()))
        viewModel = InventoryViewModel(repository, context, calculateTotalBalanceUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // =========================================================================
    // Tests para totalProducto()
    // =========================================================================

    @Test
    fun `totalProducto calcula correctamente precio por cantidad`() {
        // Given
        val price = 100.0
        val quantity = 5

        // When
        val result = viewModel.totalProducto(price, quantity)

        // Then
        assertEquals(500.0, result, 0.001)
    }

    @Test
    fun `totalProducto con cantidad cero retorna cero`() {
        // Given
        val price = 100.0
        val quantity = 0

        // When
        val result = viewModel.totalProducto(price, quantity)

        // Then
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `totalProducto con precio decimal calcula correctamente`() {
        // Given
        val price = 99.99
        val quantity = 3

        // When
        val result = viewModel.totalProducto(price, quantity)

        // Then
        assertEquals(299.97, result, 0.001)
    }

    // =========================================================================
    // Tests para loadTotalBalance()
    // =========================================================================

    @Test
    fun `loadTotalBalance actualiza totalInventoryValue correctamente`() = runTest {
        // Given
        val expectedBalance = 5000.0
        `when`(calculateTotalBalanceUseCase.invoke()).thenReturn(expectedBalance)

        // When
        viewModel.loadTotalBalance()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(expectedBalance, viewModel.totalInventoryValue.value)
        verify(calculateTotalBalanceUseCase).invoke()
    }

    @Test
    fun `loadTotalBalance maneja excepciones y retorna cero`() = runTest {
        // Given
        `when`(calculateTotalBalanceUseCase.invoke()).thenThrow(RuntimeException("Error"))

        // When
        viewModel.loadTotalBalance()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(0.0, viewModel.totalInventoryValue.value)
    }

    // =========================================================================
    // Tests para saveInventory()
    // =========================================================================

    @Test
    fun `saveInventory llama a repository insertProduct`() = runTest {
        // Given
        val product = Product(code = 1, name = "Test", unitPrice = 100.0, quantity = 5)

        // When
        viewModel.saveInventory(product)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).insertProduct(product)
    }

    @Test
    fun `saveInventory actualiza progressState durante la operación`() = runTest {
        // Given
        val product = Product(code = 1, name = "Test", unitPrice = 100.0, quantity = 5)

        // When
        viewModel.saveInventory(product)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - progressState debe ser false al finalizar
        assertFalse(viewModel.progressState.value ?: true)
    }

    // =========================================================================
    // Tests para deleteInventory()
    // =========================================================================

    @Test
    fun `deleteInventory llama a repository deleteProduct`() = runTest {
        // Given
        val product = Product(code = 1, name = "Test", unitPrice = 100.0, quantity = 5)

        // When
        viewModel.deleteInventory(product)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).deleteProduct(product)
    }

    @Test
    fun `deleteInventory actualiza progressState durante la operación`() = runTest {
        // Given
        val product = Product(code = 1, name = "Test", unitPrice = 100.0, quantity = 5)

        // When
        viewModel.deleteInventory(product)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - progressState debe ser false al finalizar
        assertFalse(viewModel.progressState.value ?: true)
    }

    // =========================================================================
    // Tests para updateInventory()
    // =========================================================================

    @Test
    fun `updateInventory llama a repository updateProduct`() = runTest {
        // Given
        val product = Product(code = 1, name = "Test Updated", unitPrice = 150.0, quantity = 10)

        // When
        viewModel.updateInventory(product)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).updateProduct(product)
    }

    @Test
    fun `updateInventory actualiza progressState durante la operación`() = runTest {
        // Given
        val product = Product(code = 1, name = "Test Updated", unitPrice = 150.0, quantity = 10)

        // When
        viewModel.updateInventory(product)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - progressState debe ser false al finalizar
        assertFalse(viewModel.progressState.value ?: true)
    }
}
