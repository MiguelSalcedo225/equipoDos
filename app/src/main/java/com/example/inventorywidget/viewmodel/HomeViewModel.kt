package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.inventorywidget.repository.ProductRepository
import com.example.inventorywidget.model.Product

/**
 * ViewModel para la pantalla Home (lista de productos)
 * Observa la lista de productos y el saldo total del inventario
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)

    /** Lista de todos los productos como LiveData (actualización en tiempo real) */
    val allProducts: LiveData<List<Product>> = repository.allProducts.asLiveData()

    /** Valor total del inventario (observa cambios automáticamente) */
    val totalInventoryValue: LiveData<Double?> = repository.totalInventoryValue.asLiveData()
}
