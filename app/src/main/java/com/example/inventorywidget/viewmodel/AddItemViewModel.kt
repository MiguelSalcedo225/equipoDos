package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.model.Inventory
import com.example.inventorywidget.repository.InventoryRepository
import kotlinx.coroutines.launch

class AddItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository(application)
    private val _saveResult = MutableLiveData<SaveResult>()
    val saveResult: LiveData<SaveResult> = _saveResult

    fun saveInventory(code: String, name: String, price: String, quantity: String) {
        viewModelScope.launch {
            try {
                // Validaciones según HU4
                if (!code.matches(Regex("^\\d{1,4}$"))) {
                    _saveResult.value = SaveResult.Error("El código debe tener 1 a 4 dígitos numéricos")
                    return@launch
                }

                val existing = repository.getInventoryByCode(code.toInt())
                if (existing != null) {
                    _saveResult.value = SaveResult.Error("Ya existe un producto con el código $code")
                    return@launch
                }

                if (name.isBlank()) {
                    _saveResult.value = SaveResult.Error("El nombre no puede estar vacío")
                    return@launch
                }

                val priceValue = price.toDoubleOrNull()
                if (priceValue == null || priceValue <= 0) {
                    _saveResult.value = SaveResult.Error("El precio debe ser un número mayor a 0")
                    return@launch
                }

                val quantityValue = quantity.toIntOrNull()
                if (quantityValue == null || quantityValue < 0) {
                    _saveResult.value = SaveResult.Error("La cantidad debe ser un número mayor o igual a 0")
                    return@launch
                }

                val inventory = Inventory(
                    code = code.toInt(),
                    name = name,
                    price = priceValue,
                    quantity = quantityValue
                )

                repository.insertInventory(inventory)
                _saveResult.value = SaveResult.Success

            } catch (e: Exception) {
                _saveResult.value = SaveResult.Error("Error al guardar: ${e.message}")
            }
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }
}

sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}
