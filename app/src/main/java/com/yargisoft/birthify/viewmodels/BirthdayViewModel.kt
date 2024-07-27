package com.yargisoft.birthify.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {

    private val _isLoaded = MutableStateFlow<Boolean?>(null)
    val isLoaded: StateFlow<Boolean?> get() = _isLoaded

    private val _saveBirthdayState = MutableStateFlow<Boolean?>(null)
    val saveBirthdayState: StateFlow<Boolean?> get() = _saveBirthdayState

    private val _getUserBirthdaysState = MutableStateFlow<Boolean?>(null)
    val getUserBirthdaysState: StateFlow<Boolean?> get() = _getUserBirthdaysState

    private val _getDeletedBirthdaysState = MutableStateFlow<Boolean?>(null)
    val getDeletedBirthdaysState: StateFlow<Boolean?> get() = _getDeletedBirthdaysState

    private val _updateBirthdayState = MutableStateFlow<Boolean?>(null)
    val updateBirthdayState: StateFlow<Boolean?> get() = _updateBirthdayState

    private val _deleteBirthdayState = MutableStateFlow<Boolean?>(null)
    val deleteBirthdayState: StateFlow<Boolean?> get() = _deleteBirthdayState

    private val _reSaveDeletedBirthdayState = MutableStateFlow<Boolean?>(null)
    val reSaveDeletedBirthdayState: StateFlow<Boolean?> get() = _reSaveDeletedBirthdayState

    private val _permanentlyDeleteBirthdayState = MutableStateFlow<Boolean?>(null)
    val permanentlyDeleteBirthdayState: StateFlow<Boolean?> get() = _permanentlyDeleteBirthdayState



    private val _birthdays = MutableLiveData<List<Birthday>>(emptyList())
    val birthdays: LiveData<List<Birthday>> get() = _birthdays

    private val _deletedBirthdayList = MutableLiveData<List<Birthday>>(emptyList())
    val deletedBirthdayList: LiveData<List<Birthday>> get() = _deletedBirthdayList



    fun saveBirthday(name: String, birthdayDate: String, note: String, userId: String) {
        Log.e("HATA","BAŞTA ${_deleteBirthdayState.value} ${_isLoaded.value}")

        viewModelScope.launch {
            Log.e("HATA","viewbaşı ${_deleteBirthdayState.value} ${_isLoaded.value}")

            try {
                Log.e("HATA","trybaşı ${_deleteBirthdayState.value} ${_isLoaded.value}")

                val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
                val birthday = Birthday(name = name, birthdayDate = birthdayDate, recordedDate = recordedDate, note = note, userId = userId)
                repository.saveBirthday(birthday) { isSuccess ->
                    _saveBirthdayState.value = isSuccess
                    _isLoaded.value = true
                    Log.e("HATA","içerde ${_deleteBirthdayState.value} ${_isLoaded.value}")

                    /*isSuccess bir değer dönderene kadar burası ve bir üstümüzdeki isLoaded kodu
                     çalışmaz. isSuccess bir yanıt dönderdiğinde isLoaded kodu  true olur, UI güncellenir
                     ve hemen ardından state ve isLoaded değeri null yapılır (aşağıdaki gibi)
                     Çünkü null yapılmazsa bu fonksiyonun bir sonraki çağrılışında doğru değer dönmeyecek ve UI
                     işlemleri sağlıklı şekilde tamamlanmayacak. */

                    _isLoaded.value = null
                    _saveBirthdayState.value = null
                }
            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _saveBirthdayState.value = false
            }
        }
        Log.e("HATA","fonks sonu ${_deleteBirthdayState.value} ${_isLoaded.value}")

    }


    fun updateBirthday(birthday: Birthday) {
        viewModelScope.launch {
            try {
                repository.updateBirthday(birthday) { isSuccess ->
                    _updateBirthdayState.value = isSuccess
                    _isLoaded.value = true

                    /*isSuccess bir değer dönderene kadar burası ve bir üstümüzdeki isLoaded kodu
                     çalışmaz. isSuccess bir yanıt dönderdiğinde isLoaded kodu  true olur, UI güncellenir
                     ve hemen ardından state ve isLoaded değeri null yapılır (aşağıdaki gibi)
                     Çünkü null yapılmazsa bu fonksiyonun bir sonraki çağrılışında doğru değer dönmeyecek ve UI
                     işlemleri sağlıklı şekilde tamamlanmayacak. */

                    _isLoaded.value = null
                    _updateBirthdayState.value = null

                }
            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _updateBirthdayState.value = false
            }
        }
    }

    fun deleteBirthday(birthdayId: String, birthday: Birthday) {
        Log.e("HATA","BAŞTA ${_deleteBirthdayState.value} ${_isLoaded.value}")
        viewModelScope.launch {
            Log.e("HATA","viewbaşı ${_deleteBirthdayState.value} ${_isLoaded.value}")
            try {
                Log.e("HATA","trybaşı ${_deleteBirthdayState.value} ${_isLoaded.value}")

                repository.deleteBirthday(birthdayId, birthday) { isSuccess ->
                    _deleteBirthdayState.value = isSuccess
                    _isLoaded.value = true
                    Log.e("HATA","içerde ${_deleteBirthdayState.value} ${_isLoaded.value}")

                    /*isSuccess bir değer dönderene kadar burası ve bir üstümüzdeki isLoaded kodu
                     çalışmaz. isSuccess bir yanıt dönderdiğinde isLoaded kodu  true olur, UI güncellenir
                     ve hemen ardından state ve isLoaded değeri null yapılır (aşağıdaki gibi)
                     Çünkü null yapılmazsa bu fonksiyonun bir sonraki çağrılışında doğru değer dönmeyecek ve UI
                     işlemleri sağlıklı şekilde tamamlanmayacak. */

                    _isLoaded.value = null
                    _deleteBirthdayState.value = null

                }

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _deleteBirthdayState.value = false
            }
        }
        Log.e("HATA","fonks sonu ${_deleteBirthdayState.value} ${_isLoaded.value}")

    }



    fun getUserBirthdays(userId: String) {
        viewModelScope.launch {
            try {
                repository.getUserBirthdays(userId) { birthdays, isSuccess ->
                    _birthdays.value = birthdays.sortedByDescending { it.recordedDate }

                    _getUserBirthdaysState.value = isSuccess
                    _isLoaded.value = true

                    /*isSuccess bir değer dönderene kadar burası ve bir üstümüzdeki isLoaded kodu
                   çalışmaz. isSuccess bir yanıt dönderdiğinde isLoaded kodu  true olur, UI güncellenir
                   ve hemen ardından state ve isLoaded değeri null yapılır (aşağıdaki gibi)
                   Çünkü null yapılmazsa bu fonksiyonun bir sonraki çağrılışında doğru değer dönmeyecek ve UI
                   işlemleri sağlıklı şekilde tamamlanmayacak. */

                    _isLoaded.value = null
                    _getUserBirthdaysState.value = null

                }
            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _getUserBirthdaysState.value = false
            }
        }
    }

    fun getDeletedBirthdays(userId: String) {
        viewModelScope.launch {
            try {
                repository.getDeletedBirthdays(userId) { birthdays, isSuccess ->

                    _deletedBirthdayList.value = birthdays.sortedByDescending { it.recordedDate }

                    _getDeletedBirthdaysState.value = isSuccess
                    _isLoaded.value = true

                    /*isSuccess bir değer dönderene kadar burası ve bir üstümüzdeki isLoaded kodu
                  çalışmaz. isSuccess bir yanıt dönderdiğinde isLoaded kodu  true olur, UI güncellenir
                  ve hemen ardından state ve isLoaded değeri null yapılır (aşağıdaki gibi)
                  Çünkü null yapılmazsa bu fonksiyonun bir sonraki çağrılışında doğru değer dönmeyecek ve UI
                  işlemleri sağlıklı şekilde tamamlanmayacak. */

                    _isLoaded.value = null
                    _getDeletedBirthdaysState.value = null

                }
            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _getDeletedBirthdaysState.value = false
            }
        }
    }

    fun reSaveDeletedBirthday(birthdayId: String, birthday: Birthday) {
        viewModelScope.launch {
            try {
                repository.reSaveDeletedBirthday(birthdayId, birthday) { isSuccess ->
                    _reSaveDeletedBirthdayState.value = isSuccess
                    _isLoaded.value = true

                    /*isSuccess bir değer dönderene kadar burası ve bir üstümüzdeki isLoaded kodu
                 çalışmaz. isSuccess bir yanıt dönderdiğinde isLoaded kodu  true olur, UI güncellenir
                 ve hemen ardından state ve isLoaded değeri null yapılır (aşağıdaki gibi)
                 Çünkü null yapılmazsa bu fonksiyonun bir sonraki çağrılışında doğru değer dönmeyecek ve UI
                 işlemleri sağlıklı şekilde tamamlanmayacak. */

                    _isLoaded.value = null
                    _reSaveDeletedBirthdayState.value = null

                }

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _reSaveDeletedBirthdayState.value = false
            }
        }
    }

    fun deleteBirthdayPermanently(birthdayId: String) {
        viewModelScope.launch {
            try {
                repository.deleteBirthdayPermanently(birthdayId) { isSuccess ->
                    _permanentlyDeleteBirthdayState.value = isSuccess
                    _isLoaded.value = true

                    /*isSuccess bir değer dönderene kadar burası ve bir üstümüzdeki isLoaded kodu
                 çalışmaz. isSuccess bir yanıt dönderdiğinde isLoaded kodu  true olur, UI güncellenir
                 ve hemen ardından state ve isLoaded değeri null yapılır (aşağıdaki gibi)
                 Çünkü null yapılmazsa bu fonksiyonun bir sonraki çağrılışında doğru değer dönmeyecek ve UI
                 işlemleri sağlıklı şekilde tamamlanmayacak. */

                    _isLoaded.value = null
                    _permanentlyDeleteBirthdayState.value = null
                }

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _permanentlyDeleteBirthdayState.value = false
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
