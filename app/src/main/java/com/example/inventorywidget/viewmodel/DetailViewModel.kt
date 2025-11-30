package com.example.inventorywidget.viewmodel



import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.domain.usecase.CalculateTotalBalanceUseCase
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * ViewModel para el DetailFragment
 * Maneja la lógica de carga y eliminación de productos
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val calculateTotalBalanceUseCase: CalculateTotalBalanceUseCase

): ViewModel() {



    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product




    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    /**
     * Carga un producto específico por su código
     */
    fun loadProduct(id: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val foundProduct = repository.getProductByCode(id)
                _product.value = foundProduct
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar el producto: ${e.message}"
            }
        }
    }



    /**
     * Elimina un producto por su código
     */
    fun deleteProduct(id: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val product = repository.getProductByCode(id)
                if (product != null) {
                    repository.deleteProduct(product)
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar el producto: ${e.message}"
            }
        }
    }


}


