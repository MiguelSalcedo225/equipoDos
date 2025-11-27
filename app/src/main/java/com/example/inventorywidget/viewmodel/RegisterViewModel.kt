package com.example.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.model.User
import com.example.inventorywidget.repository.AuthRepository
import com.example.inventorywidget.utils.Resource
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _registerState = MutableLiveData<Resource<User>>()
    val registerState: LiveData<Resource<User>> = _registerState

    fun register(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _registerState.value = Resource.Error("Todos los campos son requeridos")
            return
        }

        if (password.length < 6) {
            _registerState.value = Resource.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        _registerState.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.register(email, password)
            _registerState.value = result
        }
    }
}