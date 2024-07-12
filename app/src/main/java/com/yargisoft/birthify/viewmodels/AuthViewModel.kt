package com.yargisoft.birthify.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.repositories.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState


    private val _registrationState = MutableLiveData<Boolean>()
    val registrationState: LiveData<Boolean> get() = _registrationState

    private val _resetPasswordState = MutableLiveData<Boolean>()
    val resetPasswordState: LiveData<Boolean> get() = _resetPasswordState

    private val repository = AuthRepository()


    fun resetPassword(email: String) {
        viewModelScope.launch {
            val result = repository.resetPassword(email)
            _resetPasswordState.postValue(result)
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _loginState.postValue(result)
        }
    }

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(name, email, password)
            _registrationState.postValue(result)
        }
    }




}