package com.yargisoft.birthify.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.model.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BirthdayViewModel() : ViewModel() {
    private val _saveBirthdayState = MutableLiveData<Boolean>()
    val saveBirthdayState: LiveData<Boolean> get() = _saveBirthdayState
    private val repository = BirthdayRepository()

    fun saveBirthday(name: String, birthdayDate: String, note: String) {

        val recordedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val birthday = Birthday(name = name, birthdayDate = birthdayDate, recordedDate = recordedDate, note = note)


        viewModelScope.launch {
            val result = repository.saveBirthday(birthday)
            _saveBirthdayState.postValue(result)
        }
    }
}
