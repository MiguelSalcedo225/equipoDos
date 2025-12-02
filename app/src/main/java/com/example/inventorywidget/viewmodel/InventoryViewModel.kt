package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import com.example.inventorywidget.view.InventoryWidgetProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val repository = ProductRepository(FirebaseFirestore.getInstance())

    /** Lista del inventario observada en tiempo real */
    val listProduct: LiveData<List<Product>> = repository.allProducts().asLiveData()

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> = _progressState

    /** Valor total del inventario calculado */
    val totalInventoryValue: LiveData<Double> = repository.allProducts()
        .map { products -> products.sumOf { it.unitPrice * it.quantity } }
        .asLiveData()

    fun saveInventory(product: Product) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.insertProduct(product)
                // Pequeño delay para que Firestore sincronice antes de actualizar el widget
                delay(300)
                InventoryWidgetProvider.updateAllWidgets(context)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun deleteInventory(product: Product) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.deleteProduct(product)
                // Pequeño delay para que Firestore sincronice antes de actualizar el widget
                delay(300)
                InventoryWidgetProvider.updateAllWidgets(context)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun updateInventory(product: Product) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.updateProduct(product)
                // Pequeño delay para que Firestore sincronice antes de actualizar el widget
                delay(300)
                InventoryWidgetProvider.updateAllWidgets(context)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun totalProducto(price: Double, quantity: Int): Double {
        return price * quantity
    }
}
