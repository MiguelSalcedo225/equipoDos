package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import com.example.inventorywidget.viewmodel.AddItemViewModel
import com.example.inventorywidget.viewmodel.SaveResult
import com.example.inventorywidget.view.WidgetUpdateHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class AddItemViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock lateinit var repository: ProductRepository
    @Mock lateinit var observer: Observer<SaveResult>
    @Mock lateinit var application: Application

    @Mock lateinit var widgetUpdater: WidgetUpdateHandler

    private lateinit var viewModel: AddItemViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        viewModel = AddItemViewModel(repository, application, widgetUpdater)
        viewModel.saveResult.observeForever(observer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // TEST 1: Código inválido
    // -------------------------------------------------------------------------
    @Test
    fun `codigo invalido debe dar error`() = runTest {
        viewModel.saveInventory("abcd", "Nombre", "10", "2")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(
            SaveResult.Error("El código debe tener 1 a 4 dígitos numéricos")
        )
        verify(repository, never()).insertProduct(any())
    }

    // -------------------------------------------------------------------------
    // TEST 2: Código repetido
    // -------------------------------------------------------------------------
    @Test
    fun `codigo ya existente debe dar error`() = runTest {
        val existing = Product(1, "Viejo", 10.0, 2)
        `when`(repository.getProductByCode(1)).thenReturn(existing)

        viewModel.saveInventory("1", "Nuevo", "20", "3")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(
            SaveResult.Error("Ya existe un producto con el código 1")
        )
        verify(repository).getProductByCode(1)
        verify(repository, never()).insertProduct(any())
    }

    // -------------------------------------------------------------------------
    // TEST 3: Precio inválido
    // -------------------------------------------------------------------------
    @Test
    fun `precio no numerico o menor igual a 0 debe dar error`() = runTest {
        `when`(repository.getProductByCode(1)).thenReturn(null)

        viewModel.saveInventory("1", "Nombre", "-5", "3")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(
            SaveResult.Error("El precio debe ser un número mayor a 0")
        )
    }

    // -------------------------------------------------------------------------
    // TEST 4: Cantidad inválida
    // -------------------------------------------------------------------------
    @Test
    fun `cantidad no numerica o negativa debe dar error`() = runTest {
        `when`(repository.getProductByCode(1)).thenReturn(null)

        viewModel.saveInventory("1", "Nombre", "10", "-1")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(
            SaveResult.Error("La cantidad debe ser un número mayor o igual a 0")
        )
    }

    // -------------------------------------------------------------------------
    // TEST 5: Inserción exitosa
    // -------------------------------------------------------------------------
    @Test
    fun `insercion exitosa debe retornar Success`() = runTest {
        // El código no existe aún
        whenever(repository.getProductByCode(1)).thenReturn(null)

        // Ejecutar método
        viewModel.saveInventory("1", "Nuevo", "10", "2")

        // Avanzar corutinas
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificar inserción en base de datos
        verify(repository).insertProduct(
            Product(
                code = 1,
                name = "Nuevo",
                unitPrice = 10.0,
                quantity = 2
            )
        )
        // Verificar resultado emitido
        verify(observer).onChanged(SaveResult.Success)

        verify(widgetUpdater).update(application)
    }
    // -------------------------------------------------------------------------
    // TEST 6: Excepción del repositorio
    // -------------------------------------------------------------------------
    @Test
    fun `si insertProduct lanza excepcion debe retornar Error`() = runTest {
        `when`(repository.getProductByCode(1)).thenReturn(null)
        `when`(repository.insertProduct(any())).thenThrow(RuntimeException("Firestore error"))

        viewModel.saveInventory("1", "Nombre", "10", "2")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(
            SaveResult.Error("Error al guardar: Firestore error")
        )
    }
}
