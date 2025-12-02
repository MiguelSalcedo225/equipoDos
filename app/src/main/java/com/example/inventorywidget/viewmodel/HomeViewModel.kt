package com.example.inventorywidget.viewmodel

import androidx.lifecycle.*
import com.example.inventorywidget.domain.usecase.CalculateTotalBalanceUseCase
import com.example.inventorywidget.repository.ProductRepository
import com.example.inventorywidget.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

    private val repository: ProductRepository,
    private val calculateTotalBalanceUseCase: CalculateTotalBalanceUseCase

) : ViewModel() {



    /** Estado de carga */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    /** Lista de productos */
    private val _allProducts = MutableLiveData<List<Product>>()
    val allProducts: LiveData<List<Product>> get() = _allProducts


    // 2. Create MutableLiveData (Backing property)
    private val _totalInventoryPrice = MutableLiveData<Double?>()

    // 3. Expose as immutable LiveData
    val totalInventoryValue: LiveData<Double?> = _totalInventoryPrice

    init {
        loadTotalBalance()
    }

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
}