package com.example.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.domain.usecase.RegisterUseCase
import com.example.inventorywidget.model.User
import com.example.inventorywidget.utils.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

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
            val result = registerUseCase(email, password)
            _registerState.value = result
        }
    }
}