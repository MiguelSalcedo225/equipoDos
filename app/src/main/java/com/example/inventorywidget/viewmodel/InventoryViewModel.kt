package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import com.example.inventorywidget.utils.WidgetUpdateHelper
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val repository = ProductRepository(application)

    /** Lista del inventario observada en tiempo real */
    val listProduct: LiveData<List<Product>> = repository.allProducts.asLiveData()

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> = _progressState

    val totalInventoryValue: LiveData<Double?> =
        repository.totalInventoryValue.asLiveData()

    fun saveInventory(product: Product) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.insertProduct(product)
                // Actualizar widget cuando se guarda un producto
                WidgetUpdateHelper.updateWidget(context)
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
                // Actualizar widget cuando se elimina un producto
                WidgetUpdateHelper.updateWidget(context)
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
                // Actualizar widget cuando se actualiza un producto
                WidgetUpdateHelper.updateWidget(context)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun totalProducto(price: Double, quantity: Int): Double {
        return price * quantity
    }
}
