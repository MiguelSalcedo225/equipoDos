package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory para crear instancias de AddItemViewModel
 * Proporciona el Application que requiere el constructor del ViewModel
 */
class AddItemViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddItemViewModel::class.java)) {
            AddItemViewModel(application) as T
        } else {
            throw IllegalArgumentException("Clase ViewModel no encontrada")
        }
    }
}
