package com.example.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorywidget.model.User
import com.example.inventorywidget.repository.AuthRepository
import com.example.inventorywidget.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _loginState = MutableLiveData<Resource<User>>()
    val loginState: LiveData<Resource<User>> = _loginState

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = Resource.Error("Email and password are required")
            return
        }

        _loginState.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginState.value = result
        }
    }



    fun logout(){
        viewModelScope.launch {
            repository.logout()

        }


    }

    fun verifyUserIsLoggedIn(): Boolean {
        return repository.currentUser != null

    }

}