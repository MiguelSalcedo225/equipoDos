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
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class InventoryViewModel @Inject constructor(

    private val repository: ProductRepository,
    @ApplicationContext private val context: Context,
    private val calculateTotalBalanceUseCase: CalculateTotalBalanceUseCase

) : ViewModel() {

    val listProduct: LiveData<List<Product>> = repository.allProducts().asLiveData()

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> = _progressState

    private val _totalInventoryPrice = MutableLiveData<Double?>()
    val totalInventoryValue: LiveData<Double?> = _totalInventoryPrice

    fun saveInventory(product: Product) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.insertProduct(product)
                WidgetUpdateHelper.updateWidget(context)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun loadTotalBalance() {
        viewModelScope.launch {
            try {
                val total = calculateTotalBalanceUseCase()
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
                WidgetUpdateHelper.updateWidget(context)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun totalProducto(price: Double, quantity: Int): Double =
        price * quantity
}
