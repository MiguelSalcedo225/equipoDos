package com.example.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.model.Inventory
import com.example.inventorywidget.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository(application)

    private val _listInventory = MutableLiveData<List<Inventory>>()
    val listInventory: LiveData<List<Inventory>> get() = _listInventory

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> = _progressState

    val totalInventoryValue: LiveData<Double?> =
        repository.getTotalInventoryValue().asLiveData()

    fun saveInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.insertInventory(inventory)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun getListInventory() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _listInventory.value = repository.getAllInventory()
            } finally {
                _progressState.value = false
            }
        }
    }

    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.deleteInventory(inventory)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.updateInventory(inventory)
            } finally {
                _progressState.value = false
            }
        }
    }

    fun totalProducto(price: Double, quantity: Int): Double {
        return price * quantity
    }
}
