package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.inventorywidget.repository.ProductRepository
import com.example.inventorywidget.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(FirebaseFirestore.getInstance())

    /** Estado de carga */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    /** Lista de productos */
    private val _allProducts = MutableLiveData<List<Product>>()
    val allProducts: LiveData<List<Product>> get() = _allProducts

    /** Valor total del inventario calculado */
    val totalInventoryValue: LiveData<Double> = repository.allProducts()
        .map { products -> products.sumOf { it.unitPrice * it.quantity } }
        .asLiveData()

    init {
        loadProducts()
    }
    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            // Simular tiempo de carga
            delay(2000)
            repository.allProducts().collect { productList ->
                _allProducts.value = productList
                _isLoading.value = false
            }
        }
    }
}
