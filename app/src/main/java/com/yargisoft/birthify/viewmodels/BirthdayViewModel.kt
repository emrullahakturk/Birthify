package com.yargisoft.birthify.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading


    private val _saveBirthdayState = MutableStateFlow(false)
    val saveBirthdayState: StateFlow<Boolean> get() = _saveBirthdayState

    private val _getUserBirthdaysState = MutableStateFlow(false)
    val getUserBirthdaysState: StateFlow<Boolean> get() = _getUserBirthdaysState

     private val _getDeletedBirthdaysState = MutableStateFlow(false)
    val getDeletedBirthdaysState: StateFlow<Boolean> get() = _getDeletedBirthdaysState

     private val _updateBirthdayState = MutableStateFlow(false)
    val updateBirthdayState: StateFlow<Boolean> get() = _updateBirthdayState

     private val _deleteBirthdayState = MutableStateFlow(false)
    val deleteBirthdayState: StateFlow<Boolean> get() = _deleteBirthdayState

       private val _reSaveDeletedBirthdayState = MutableStateFlow(false)
    val reSaveDeletedBirthdayState: StateFlow<Boolean> get() = _reSaveDeletedBirthdayState

       private val _permanentlyDeleteBirthdayState = MutableStateFlow(false)
    val permanentlyDeleteBirthdayState: StateFlow<Boolean> get() = _permanentlyDeleteBirthdayState




    private val _birthdays = MutableLiveData<List<Birthday>>()
    val birthdays: LiveData<List<Birthday>> get() = _birthdays

    private val _deletedBirthdayList = MutableLiveData<List<Birthday>>()
    val deletedBirthdayList: LiveData<List<Birthday>> get() = _deletedBirthdayList



    fun saveBirthday(name: String, birthdayDate: String, note: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
                val birthday = Birthday(name = name, birthdayDate = birthdayDate, recordedDate = recordedDate, note = note, userId = userId)
                repository.saveBirthday(birthday) { isSuccess ->
                    _saveBirthdayState.value = isSuccess
                }
            } catch (e: Exception) {
                Log.e("exception", "$e")
                _saveBirthdayState.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateBirthday(birthday: Birthday) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateBirthday(birthday) { isSuccess ->
                    _updateBirthdayState.value = isSuccess
                }
            } catch (e: Exception) {
                Log.e("exception", "$e")
                _updateBirthdayState.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBirthday(birthdayId: String, birthday: Birthday) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteBirthday(birthdayId, birthday) { isSuccess ->
                    _deleteBirthdayState.value = isSuccess
                }
            } catch (e: Exception) {
                Log.e("exception", "$e")
                _deleteBirthdayState.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun getUserBirthdays(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getUserBirthdays(userId) { birthdays, isSuccess ->
                    _birthdays.value = birthdays.sortedByDescending { it.recordedDate }
                    _getUserBirthdaysState.value = isSuccess
                }
            } catch (e: Exception) {
                Log.e("exception", "$e")
                _getUserBirthdaysState.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getDeletedBirthdays(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getDeletedBirthdays(userId) { birthdays, isSuccess ->
                    _deletedBirthdayList.value = birthdays.sortedByDescending { it.recordedDate }
                    _getDeletedBirthdaysState.value = isSuccess
                }
            } catch (e: Exception) {
                Log.e("exception", "$e")
                _getDeletedBirthdaysState.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reSaveDeletedBirthday(birthdayId: String, birthday: Birthday) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.reSaveDeletedBirthday(birthdayId, birthday) { isSuccess ->
                    _reSaveDeletedBirthdayState.value = isSuccess
                }
            } catch (e: Exception) {
                Log.e("exception", "$e")
                _reSaveDeletedBirthdayState.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBirthdayPermanently(birthdayId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteBirthdayPermanently(birthdayId) { isSuccess ->
                    _permanentlyDeleteBirthdayState.value = isSuccess
                }
            } catch (e: Exception) {
                Log.e("exception", "$e")
                _permanentlyDeleteBirthdayState.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    @SuppressLint("CheckResult")
    fun sortBirthdaysByNameAsc(): List<Birthday> {
        return birthdays.value?.sortedBy { it.name } ?: emptyList()
    }

    @SuppressLint("CheckResult")
    fun sortBirthdaysByNameDsc(): List<Birthday> {
        return birthdays.value?.sortedByDescending { it.name } ?: emptyList()
    }

    @SuppressLint("CheckResult")
    fun sortBirthdaysByBirthdayDateAsc(): List<Birthday> {
        return birthdays.value?.sortedBy { it.birthdayDate } ?: emptyList()
    }

    @SuppressLint("CheckResult")
    fun sortBirthdaysByBirthdayDateDsc(): List<Birthday> {
        return birthdays.value?.sortedByDescending { it.birthdayDate } ?: emptyList()
    }

    @SuppressLint("CheckResult")
    fun sortBirthdaysByRecordedDateAsc(): List<Birthday> {
        return birthdays.value?.sortedBy { it.recordedDate } ?: emptyList()
    }

    @SuppressLint("CheckResult")
    fun sortBirthdaysByRecordedDateDsc(): List<Birthday> {
        return birthdays.value?.sortedByDescending { it.recordedDate } ?: emptyList()
    }


}
