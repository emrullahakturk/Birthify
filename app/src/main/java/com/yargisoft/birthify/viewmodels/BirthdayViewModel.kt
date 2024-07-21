package com.yargisoft.birthify.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {
    private val _saveBirthdayState = MutableLiveData<Boolean>()
    val saveBirthdayState: LiveData<Boolean> get() = _saveBirthdayState

    private val _updateBirthdayState = MutableLiveData<Boolean>()
    val updateBirthdayState: LiveData<Boolean> get() = _updateBirthdayState

    private val _birthdays = MutableLiveData<List<Birthday>>()
    val birthdays: LiveData<List<Birthday>> get() = _birthdays

    private val _deletedBirthdayList = MutableLiveData<List<Birthday>>()
    val deletedBirthdayList: LiveData<List<Birthday>> get() = _deletedBirthdayList

    private val _deleteBirthdayState = MutableLiveData<Boolean>()
    val deleteBirthdayState: LiveData<Boolean> get() = _deleteBirthdayState

    private val _reSaveDeletedBirthdayState = MutableLiveData<Boolean>()
    val reSaveDeletedBirthdayState: LiveData<Boolean> get() = _reSaveDeletedBirthdayState

    private val _permanentlyDeleteBirthdayState = MutableLiveData<Boolean>()
    val permanentlyDeleteBirthdayState: LiveData<Boolean> get() = _permanentlyDeleteBirthdayState

    fun saveBirthday(name: String, birthdayDate: String, note: String, userId: String) {
        val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
        val birthday = Birthday(name = name, birthdayDate = birthdayDate, recordedDate = recordedDate, note = note, userId = userId)
        viewModelScope.launch {
            repository.saveBirthday(birthday) { isSuccess ->
                _saveBirthdayState.postValue(isSuccess)
            }
        }
    }

    fun updateBirthday(birthday: Birthday) {
        repository.updateBirthday(birthday) { isSuccess ->
            _updateBirthdayState.postValue(isSuccess)
        }
    }

    fun deleteBirthday(birthdayId: String, birthday: Birthday) {
        repository.deleteBirthday(birthdayId, birthday) { isSuccess ->
            _deleteBirthdayState.postValue(isSuccess)
        }
    }



    fun getUserBirthdays(userId: String) {
        viewModelScope.launch {
            repository.getUserBirthdays(userId) { birthdays ->
                _birthdays.postValue(birthdays.sortedByDescending { it.recordedDate })
            }
        }
    }

    fun getDeletedBirthdays(userId: String) {
        viewModelScope.launch {
            repository.getDeletedBirthdays(userId) { birthdays ->
                _deletedBirthdayList.postValue(birthdays.sortedByDescending { it.recordedDate })
            }
        }
    }


    fun reSaveDeletedBirthday(birthdayId: String, birthday: Birthday) {
        viewModelScope.launch {
            repository.reSaveDeletedBirthday(birthdayId, birthday) { isSuccess ->
                _reSaveDeletedBirthdayState.postValue(isSuccess)
            }
        }
    }

    fun deleteBirthdayPermanently(birthdayId: String) {
        repository.deleteBirthdayPermanently(birthdayId) { isSuccess ->
            _permanentlyDeleteBirthdayState.postValue(isSuccess)
        }
    }
}
