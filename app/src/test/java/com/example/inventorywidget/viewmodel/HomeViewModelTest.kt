package com.example.inventorywidget.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inventorywidget.domain.usecase.CalculateTotalBalanceUseCase
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
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
        // Given: Una lista de productos
        val mockProducts = listOf(
            Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5),
            Product(code = 1002, name = "Mouse", unitPrice = 25.0, quantity = 10)
        )
        Mockito.`when`(productRepository.allProducts()).thenReturn(flowOf(mockProducts))
        // When: Se inicializa el ViewModel (que llama loadProducts en init)
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: La lista de productos debe estar cargada
        Assert.assertEquals(mockProducts, homeViewModel.allProducts.value)
        Mockito.verify(productRepository).allProducts()
    }

    @Test
    fun `loadProducts maneja correctamente una lista vacía`() = runTest {
        // Given: Una lista vacía
        Mockito.`when`(productRepository.allProducts()).thenReturn(flowOf(emptyList()))
        // When: Se inicializa el ViewModel
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: La lista debe estar vacía
        Assert.assertNotNull(homeViewModel.allProducts.value)
        Assert.assertTrue(homeViewModel.allProducts.value?.isEmpty() ?: false)
    }

    @Test
    fun `loadProducts actualiza isLoading correctamente`() = runTest {
        // Given: Productos
        val mockProducts = listOf(
            Product(code = 1, name = "Test", unitPrice = 10.0, quantity = 1)
        )
        Mockito.`when`(productRepository.allProducts()).thenReturn(flowOf(mockProducts))
        // When: Se inicializa el ViewModel
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: isLoading debe ser false al finalizar
        Assert.assertFalse(homeViewModel.isLoading.value ?: true)
    }

    @Test
    fun `loadTotalBalance calcula el balance correctamente`() = runTest {
        // Given: Un balance esperado
        val expectedBalance = 5500.0
        val mockProducts = listOf(
            Product(code = 1, name = "Test", unitPrice = 1000.0, quantity = 5),
            Product(code = 2, name = "Test2", unitPrice = 100.0, quantity = 5)
        )
        Mockito.`when`(productRepository.allProducts()).thenReturn(flowOf(mockProducts))
        Mockito.`when`(productRepository.getProductsSnapshot()).thenReturn(mockProducts)
        Mockito.`when`(calculateTotalBalanceUseCase.invoke()).thenReturn(expectedBalance)
        // When
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()

        // Then: El balance total debe ser el esperado
        Assert.assertEquals(expectedBalance, homeViewModel.totalInventoryValue.value)
        Mockito.verify(calculateTotalBalanceUseCase).invoke()
    }

    @Test
    fun `loadTotalBalance maneja excepciones y devuelve 0`() = runTest {
        // Given: Un caso donde el use case lanza excepción
        Mockito.`when`(productRepository.allProducts()).thenReturn(flowOf(emptyList()))
        Mockito.`when`(productRepository.getProductsSnapshot()).thenReturn(emptyList())
        Mockito.`when`(calculateTotalBalanceUseCase.invoke())
            .thenThrow(RuntimeException("Error al calcular"))
        // When
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then: El balance debe ser 0.0
        Assert.assertEquals(0.0, homeViewModel.totalInventoryValue.value)
    }

    @Test
    fun `init ejecuta loadProducts y loadTotalBalance correctamente`() = runTest {
        val mockProducts = listOf(
            Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        )
        val expectedBalance = 5000.0

        Mockito.`when`(productRepository.allProducts()).thenReturn(flowOf(mockProducts))
        Mockito.`when`(calculateTotalBalanceUseCase.invoke()).thenReturn(expectedBalance)
        // When
        homeViewModel = HomeViewModel(productRepository, calculateTotalBalanceUseCase)
        advanceUntilIdle()
        // Then
        Mockito.verify(productRepository).allProducts()
        Mockito.verify(calculateTotalBalanceUseCase).invoke()
        Assert.assertEquals(mockProducts, homeViewModel.allProducts.value)
        Assert.assertEquals(expectedBalance, homeViewModel.totalInventoryValue.value)
    }
}