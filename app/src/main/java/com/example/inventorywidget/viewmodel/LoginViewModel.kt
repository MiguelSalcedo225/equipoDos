package com.example.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.domain.usecase.LoginUseCase
import com.example.inventorywidget.domain.usecase.LogoutUseCase
import com.example.inventorywidget.domain.usecase.RegisterUseCase
import com.example.inventorywidget.domain.usecase.VerifyUserIsLoggedInUseCase
import com.example.inventorywidget.model.User
import com.example.inventorywidget.utils.Resource
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val verifyUserIsLoggedInUseCase: VerifyUserIsLoggedInUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {


    private val _authState = MutableLiveData<Resource<User>>()
    val authState: LiveData<Resource<User>> = _authState

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = Resource.Error("Email and password are required")
            return
        }

        _authState.value = Resource.Loading()

        viewModelScope.launch {
            val result = loginUseCase(email, password)
            _authState.value = result
        }
    }



    fun logout(){
        viewModelScope.launch {
            logoutUseCase()

        }


    }

    fun verifyUserIsLoggedIn(): Boolean {
        return verifyUserIsLoggedInUseCase()

    }

    fun register(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = Resource.Error("Todos los campos son requeridos")
            return
        }

        if (password.length < 6) {
            _authState.value = Resource.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        _authState.value = Resource.Loading()

        viewModelScope.launch {
            val result = registerUseCase(email, password)
            _authState.value = result
        }
    }

}