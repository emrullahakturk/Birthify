package com.yargisoft.birthify.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // StateFlow to observe the status of ongoing operations
    private var _isLoaded = MutableStateFlow(false)
    val isLoaded: MutableStateFlow<Boolean> get() = _isLoaded

    private var _authSuccess = MutableStateFlow(false)
    val authSuccess: MutableStateFlow<Boolean> get() = _authSuccess

    private var _authError = MutableStateFlow<String?>("")
    val authError: MutableStateFlow<String?> get() = _authError

    // Function to handle user profile update
    fun updateUserProfile(name: String?, password: String?) {
        viewModelScope.launch {
            _isLoaded.value = false
            authRepository.updateUserProfile(
                name,
                password,
                onSuccess = {
                    _authSuccess.value = true
                    _authError.value = null
                    _isLoaded.value = true

                },
                onFailure = { errorMessage ->
                    _authSuccess.value = false
                    _authError.value = errorMessage
                    _isLoaded.value = true

                }
            )
        }
    }

    // Function to handle user registration
    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoaded.value = false
            authRepository.registerUser(
                name, email, password,
                onSuccess = {
                    _authSuccess.value = true
                    _authError.value = null
                    _isLoaded.value = true
                },
                onFailure = { errorMessage ->
                    _authSuccess.value = false
                    _authError.value = errorMessage
                    _isLoaded.value = true
                }
            )

        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _isLoaded.value = false
            authRepository.loginUser(
                email, password,
                onSuccess = {
                    _authSuccess.value = true
                    _authError.value = null
                    _isLoaded.value = true
                },
                onFailure = { errorMessage ->
                    _authSuccess.value = false
                    _authError.value = errorMessage
                    _isLoaded.value = true
                }
            )
        }
    }

    // Function to handle password reset
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoaded.value = false
            authRepository.resetPassword(
                email,
                onSuccess = {
                    _authSuccess.value = true
                    _authError.value = null
                    _isLoaded.value = true

                },
                onFailure = { errorMessage ->
                    _authSuccess.value = false
                    _authError.value = errorMessage
                    _isLoaded.value = true

                }
            )
        }
    }

    // Function to handle user logout
    fun logoutUser() {
        _isLoaded.value = false
        viewModelScope.launch {
            authRepository.logoutUser()
            _isLoaded.value= true
        }

    }

    // Function to check user session status
    fun checkUserSession() {
        viewModelScope.launch {
            val sessionExists = authRepository.isUserLoggedIn()
            if (sessionExists) {
                _authSuccess.value = true
                _isLoaded.value= true

            } else {
                _authError.value = "No active session found."
                _isLoaded.value= true

            }
        }
    }
}
