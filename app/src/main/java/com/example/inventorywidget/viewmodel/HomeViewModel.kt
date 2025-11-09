package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.model.Inventory
import com.example.inventorywidget.repository.InventoryRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla Home (lista de productos)
 * Observa la lista de productos y el saldo total del inventario
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository(application)

    // LiveData que contiene todos los productos
    private val _allProducts = MutableLiveData<List<Inventory>>()
    val allProducts: LiveData<List<Inventory>> get() = _allProducts

    // LiveData que contiene el saldo total del inventario
    val totalInventoryValue: LiveData<Double?> = repository.getTotalInventoryValue().asLiveData()

    init {
        loadAllProducts()
    }

    private fun loadAllProducts() {
        viewModelScope.launch {
            _allProducts.value = repository.getAllInventory()
        }
    }
}
