package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.domain.usecase.CalculateTotalBalanceUseCase
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import com.example.inventorywidget.utils.WidgetUpdateHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(

    private val repository: ProductRepository,
    private val context: Application,
    private val calculateTotalBalanceUseCase: CalculateTotalBalanceUseCase

) : ViewModel() {



    /** Lista del inventario observada en tiempo real */
    val listProduct: LiveData<List<Product>> = repository.allProducts().asLiveData()

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> = _progressState

    private val _totalInventoryPrice = MutableLiveData<Double?>()

    // 3. Expose as immutable LiveData
    val totalInventoryValue: LiveData<Double?> = _totalInventoryPrice



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

    fun loadTotalBalance() {
        viewModelScope.launch {
            try {
                // Call the use case (it runs on background because of Repository)
                val total = calculateTotalBalanceUseCase()

                // Update the LiveData
                _totalInventoryPrice.value = total
            } catch (e: Exception) {
                _totalInventoryPrice.value = 0.0
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
