package com.yargisoft.birthify.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private var _isLoaded = MutableStateFlow(false)
    val isLoaded: MutableStateFlow<Boolean> get() = _isLoaded

    private var _authSuccess = MutableStateFlow(false)
    val authSuccess: MutableStateFlow<Boolean> get() = _authSuccess

    private var _authError = MutableStateFlow<String?>("")
    val authError: MutableStateFlow<String?> get() = _authError

    private var _userCredentials = MutableStateFlow<Map<String, Any>?>(null)
    val userCredentials: MutableStateFlow<Map<String, Any>?> get() = _userCredentials



    fun updatePassword(currentPassword: String, newPassword:String){
        viewModelScope.launch {
            _isLoaded.value = false
            authRepository.updateUserPassword(
                currentPassword,
                newPassword,
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


    fun deleteUserAccount() {
        viewModelScope.launch {
            _isLoaded.value = false
            authRepository.deleteUserAccount(
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

    fun getUserCredentials() {
        viewModelScope.launch {
            _isLoaded.value = false
            authRepository.getUserCredentials(
                onSuccess = { credentials ->
                    _userCredentials.value = credentials
                    _authSuccess.value = true
                    _authError.value = null
                    _isLoaded.value = true
                },
                onFailure = { errorMessage ->
                     _userCredentials.value = null
                    _authSuccess.value = false
                    _authError.value = errorMessage
                    _isLoaded.value = true
                }
            )
        }
    }

}
