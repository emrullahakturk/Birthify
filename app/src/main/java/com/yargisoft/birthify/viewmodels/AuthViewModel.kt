package com.yargisoft.birthify.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.repositories.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    private val _registrationState = MutableLiveData<Boolean>()
    val registrationState: LiveData<Boolean> get() = _registrationState

    private val _resetPasswordState = MutableLiveData<Boolean>()
    val resetPasswordState: LiveData<Boolean> get() = _resetPasswordState

    private val _emailVerificationState = MutableLiveData<Boolean>()
    val emailVerificationState: LiveData<Boolean> get() = _emailVerificationState

    fun resetPassword(email: String) {
        viewModelScope.launch {
            val result = repository.resetPassword(email)
            _resetPasswordState.postValue(result)
        }
    }

    fun loginUser(email: String, password: String, isChecked: Boolean) {
        viewModelScope.launch {
            val result = repository.loginUser(email, password, isChecked)
            _loginState.postValue(result)

        }

    }

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(name, email, password)
            _registrationState.postValue(result)
        }
    }

    private fun isEmailVerified() {
        viewModelScope.launch {
            val result = repository.isEmailVerified()
            _emailVerificationState.postValue(result)
        }
    }
}
