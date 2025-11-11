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
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.repository.ProductRepository


/**
 * ViewModel para el DetailFragment
 * Maneja la lógica de carga y eliminación de productos
 */
class DetailViewModel(application: Application) : ViewModel() {

    private val repository = ProductRepository(application)

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    val totalInventoryPrice: LiveData<Double?> = repository.totalInventoryValue.asLiveData()




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


