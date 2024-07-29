package com.yargisoft.birthify.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.repositories.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {


    var isEmailVerifiedResult: Boolean? = null

    private val _isLoaded = MutableStateFlow<Boolean?>(null)
    val isLoaded: StateFlow<Boolean?> get() = _isLoaded

    private var _authViewModelState: Boolean?  = null
    val authViewModelState: Boolean? get() = _authViewModelState



    private val _isResetMailSent = MutableStateFlow<Boolean?>(null)
    val isResetMailSent: StateFlow<Boolean?> get() = _isResetMailSent




    fun resetPassword(email: String) {
        viewModelScope.launch {

            try {
                _isResetMailSent.value = repository.resetPassword(email)
                delay(2000)
                _isLoaded.value = true
            }catch (e:Exception){
                Log.e("exception","$e")
            }

            _isResetMailSent.value = null
            _isLoaded.value = null
        }
    }

    fun loginUser(email: String, password: String, isChecked: Boolean) {
        viewModelScope.launch {
            try {

                val result = repository.loginUser(email, password, isChecked)
                _authViewModelState = result.first
                isEmailVerifiedResult = result.second

                delay(3000)

                _isLoaded.value = true


            } catch (e: Exception) {
                Log.e("exception","$e")
                _authViewModelState = false
            }

            _isLoaded.value = null
            _authViewModelState = null

        }
    }


    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
                val result = repository.registerUser(name, email, password, recordedDate)
                _authViewModelState = result

                _isLoaded.value = true

            } catch (e: Exception) {
                Log.e("exception","$e")
                _authViewModelState = false
            }

            _isLoaded.value = null
            _authViewModelState = null

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
