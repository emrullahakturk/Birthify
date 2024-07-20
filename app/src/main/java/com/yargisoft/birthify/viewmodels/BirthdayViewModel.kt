package com.yargisoft.birthify.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {
    private val _saveBirthdayState = MutableLiveData<Boolean>()
    val saveBirthdayState: LiveData<Boolean> get() = _saveBirthdayState

    private val _birthdays = MutableLiveData<List<Birthday>>()
    val birthdays: LiveData<List<Birthday>> get() = _birthdays


    private val _deletedBirthdayList = MutableLiveData<List<Birthday>>()
    val deletedBirthdayList: LiveData<List<Birthday>> get() = _deletedBirthdayList



    private val _deleteBirthdayState = MutableLiveData<Boolean>()
    val deleteBirthdayState: LiveData<Boolean> get() = _deleteBirthdayState


    fun saveBirthday(name: String, birthdayDate: String, note: String,userId: String) {

        val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
        val birthday = Birthday(name = name, birthdayDate = birthdayDate, recordedDate = recordedDate, note = note, userId = userId)

        viewModelScope.launch {
            val result = repository.saveBirthday(birthday)
            _saveBirthdayState.postValue(result)
        }
    }

    fun updateBirthday(birthday: Birthday, onComplete: (Boolean) -> Unit) {
        repository.updateBirthday(birthday, onComplete)
    }


    fun deleteBirthday(birthdayId: String, birthday: Birthday) {
        repository.deleteBirthday(birthdayId, birthday) { isSuccess ->
            _deleteBirthdayState.postValue(isSuccess)
        }
    }


    fun getUserBirthdays(userId: String) {
        viewModelScope.launch {
            val result = repository.getUserBirthdays(userId).sortedByDescending { it.recordedDate }
            _birthdays.postValue(result)
        }
    }

    fun getDeletedBirthdays(userId: String) {
         viewModelScope.launch {
            val result = repository.getDeletedBirthdays(userId).sortedByDescending { it.recordedDate }
             _deletedBirthdayList.postValue(result)
        }
    }
}
