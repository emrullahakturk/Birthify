package com.yargisoft.birthify.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {



    var isResetMailSended = false
    var isEmailVerifiedResult: Boolean? = null

    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> get() = _isLoading
    private val _registrationResult = MutableStateFlow<Boolean?>(null)

    val registrationResult: StateFlow<Boolean?> get() = _registrationResult
    private val _loginResult = MutableStateFlow<Boolean?>(null)

    val loginResult: StateFlow<Boolean?> get() = _loginResult

    fun resetPassword(email: String):Boolean {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                isResetMailSended= repository.resetPassword(email)

            }catch (e:Exception){
                //hata mesajı
            }finally {
                _isLoading.value = false
            }
        }
        return isResetMailSended
    }

    fun loginUser(email: String, password: String, isChecked: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.loginUser(email, password, isChecked)

                _loginResult.value = result.first
                isEmailVerifiedResult = result.second
            } catch (e: Exception) {
                Log.e("exception","$e")
                _loginResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
                val result = repository.registerUser(name, email, password, recordedDate)
                _registrationResult.value = result
            } catch (e: Exception) {
                Log.e("exception","$e")
                _registrationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logoutUser() {
        repository.logout()
    }

    fun loggedUserId() : String {
        var result = ""
        viewModelScope.launch {
           result = repository.loggedUserId()
        }
        return result
    }
}
