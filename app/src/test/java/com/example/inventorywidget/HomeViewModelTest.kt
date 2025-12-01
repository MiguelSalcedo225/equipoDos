package com.example.inventorywidget

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inventorywidget.domain.usecase.CalculateTotalBalanceUseCase
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import com.example.inventorywidget.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class HomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var homeViewModel: HomeViewModel

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var calculateTotalBalanceUseCase: CalculateTotalBalanceUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts carga correctamente la lista de productos`() = runTest {
        // Given: Una lista de productos simulada
        val mockProducts = listOf(
            Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5),
            Product(code = 1002, name = "Mouse", unitPrice = 25.0, quantity = 10)
        )
        `when`(productRepository.allProducts()).thenReturn(flowOf(mockProducts))
        // When: Se inicializa el ViewModel (que llama loadProducts en init)
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: La lista de productos debe estar cargada
        assertEquals(mockProducts, homeViewModel.allProducts.value)
        verify(productRepository).allProducts()
    }

    @Test
    fun `loadProducts maneja correctamente una lista vacía`() = runTest {
        // Given: Una lista vacía
        `when`(productRepository.allProducts()).thenReturn(flowOf(emptyList()))
        // When: Se inicializa el ViewModel
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: La lista debe estar vacía pero no null
        assertNotNull(homeViewModel.allProducts.value)
        assertTrue(homeViewModel.allProducts.value?.isEmpty() ?: false)
    }

    @Test
    fun `loadProducts actualiza isLoading correctamente`() = runTest {
        // Given: Productos mock
        val mockProducts = listOf(
            Product(code = 1, name = "Test", unitPrice = 10.0, quantity = 1)
        )
        `when`(productRepository.allProducts()).thenReturn(flowOf(mockProducts))
        // When: Se inicializa el ViewModel
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: isLoading debe ser false al finalizar
        assertFalse(homeViewModel.isLoading.value ?: true)
    }

    @Test
    fun `loadTotalBalance calcula el balance correctamente`() = runTest {
        // Given: Un balance esperado
        val expectedBalance = 5500.0
        `when`(productRepository.allProducts()).thenReturn(flowOf(emptyList()))
        `when`(calculateTotalBalanceUseCase.invoke()).thenReturn(expectedBalance)
        // When: Se inicializa el ViewModel (que llama loadTotalBalance en init)
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: El balance total debe ser el esperado
        assertEquals(expectedBalance, homeViewModel.totalInventoryValue.value)
        verify(calculateTotalBalanceUseCase).invoke()
    }

    @Test
    fun `loadTotalBalance maneja excepciones y devuelve 0`() = runTest {
        // Given: Un caso donde el use case lanza excepción
        `when`(productRepository.allProducts()).thenReturn(flowOf(emptyList()))
        `when`(calculateTotalBalanceUseCase.invoke())
            .thenThrow(RuntimeException("Error al calcular"))
        // When: Se inicializa el ViewModel
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: El balance debe ser 0.0 (valor por defecto en caso de error)
        assertEquals(0.0, homeViewModel.totalInventoryValue.value)
    }

    @Test
    fun `init ejecuta loadProducts y loadTotalBalance correctamente`() = runTest {
        val mockProducts = listOf(
            Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        )
        val expectedBalance = 5000.0

        `when`(productRepository.allProducts()).thenReturn(flowOf(mockProducts))
        `when`(calculateTotalBalanceUseCase.invoke()).thenReturn(expectedBalance)
        // When: Se inicializa el ViewModel
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: Ambos métodos deben haber sido llamados y sus valores actualizados
        verify(productRepository).allProducts()
        verify(calculateTotalBalanceUseCase).invoke()
        assertEquals(mockProducts, homeViewModel.allProducts.value)
        assertEquals(expectedBalance, homeViewModel.totalInventoryValue.value)
    }
}