package com.yargisoft.birthify.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {




    private val _birthdayList = MutableLiveData<List<Birthday>>()
    val birthdayList: LiveData<List<Birthday>> get() = _birthdayList

    private val _deletedBirthdayList = MutableLiveData<List<Birthday>>()
    val deletedBirthdayList: LiveData<List<Birthday>> get() = _deletedBirthdayList


    fun saveBirthday(birthday: Birthday){
        repository.saveBirthday(birthday)
    }
    fun deleteBirthday(birthdayId:String){
        repository.deleteBirthday(birthdayId )
    }
    fun updateBirthday(birthday: Birthday){
        repository.updateBirthday(birthday)
    }
    fun getBirthdays(){
       _birthdayList.value =  repository.getBirthdays()
    }
    fun clearBirthdays(){
        repository.clearBirthdays()
    }


    fun permanentlyDeleteBirthday(birthdayId: String){
        repository.permanentlyDeleteBirthday(birthdayId )
    }
    fun reSaveDeletedBirthday(birthdayId: String){
        repository.reSaveDeletedBirthday(birthdayId)
    }
    fun getDeletedBirthdays(){
       _deletedBirthdayList.value =  repository.getDeletedBirthdays()
    }
    fun clearDeletedBirthdays(){
        repository.clearDeletedBirthdays()
    }




/*

    private val _isLoaded = MutableStateFlow<Boolean?>(null)
    val isLoaded: StateFlow<Boolean?> get() = _isLoaded

    
    private var _birthdayViewModelState :Boolean? = null
    val birthdayViewModelState :Boolean? get() = _birthdayViewModelState


    private val _deletedBirthdayList = MutableLiveData<List<Birthday>>(emptyList())
    val deletedBirthdayList: LiveData<List<Birthday>> get() = _deletedBirthdayList


    fun saveBirthdayPref(name: String, birthdayDate: String, note: String, userId: String){
        repository.saveBirthdayPref(name,birthdayDate, note, userId)
    }

    fun saveBirthday(name: String, birthdayDate: String, note: String, userId: String) {
        viewModelScope.launch {
            try {
                val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
                val birthday = Birthday(name = name, birthdayDate = birthdayDate, recordedDate = recordedDate, note = note, userId = userId)

                repository.saveBirthday(birthday) { isSuccess ->
                    _birthdayViewModelState = isSuccess
                }
                delay(3000)

                _isLoaded.value = true
                */
/*isSuccess bir değer dönderene kadar burası ve bir üstümüzdeki isLoaded kodu
                 çalışmaz. isSuccess bir yanıt dönderdiğinde isLoaded kodu  true olur, UI güncellenir
                 ve hemen ardından state ve isLoaded değeri null yapılır (aşağıdaki gibi)
                 Çünkü null yapılmazsa bu fonksiyonun bir sonraki çağrılışında doğru değer dönmeyecek ve UI
                 işlemleri sağlıklı şekilde tamamlanmayacak. */
    /*


            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _birthdayViewModelState = false
            }

            //viewModelScope bittiğinde değerler tekrar null olmalı
            _isLoaded.value = null
            _birthdayViewModelState = null

        }

    }


    fun updateBirthday(birthday: Birthday) {
        viewModelScope.launch {

            try {
                repository.updateBirthday(birthday) { isSuccess ->
                    _birthdayViewModelState = isSuccess
                }

                delay(3000)
                _isLoaded.value = true

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _birthdayViewModelState = false
            }

            //viewModelScope bittiğinde değerler tekrar null olmalı
            _isLoaded.value = null
            _birthdayViewModelState = null
        }
    }



    fun deleteBirthday(birthdayId: String, birthday: Birthday) {
        viewModelScope.launch {
            try {
                repository.deleteBirthday(birthdayId, birthday) { isSuccess ->
                    _birthdayViewModelState = isSuccess
                }
                
                delay(3000)
                _isLoaded.value = true

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _birthdayViewModelState = false
            }

            //viewModelScope bittiğinde değerler tekrar null olmalı
            _isLoaded.value = null
            _birthdayViewModelState = null
        }
    }



    fun getUserBirthdays(userId: String) {
        viewModelScope.launch {
            try {
                repository.getUserBirthdays(userId) { birthdays, isSuccess ->
                    _birthdayList.value = birthdays.sortedByDescending { it.recordedDate }
                    _birthdayViewModelState = isSuccess
                }

                _isLoaded.value = true

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _birthdayViewModelState = false
            }
            //viewModelScope bittiğinde değerler tekrar null olmalı
            _isLoaded.value = null
            _birthdayViewModelState = null
        }
    }


    fun getDeletedBirthdays(userId: String) {
        viewModelScope.launch {
            try {
                repository.getDeletedBirthdays(userId) { birthdays, isSuccess ->

                    _deletedBirthdayList.value = birthdays.sortedByDescending { it.recordedDate }
                    _birthdayViewModelState = isSuccess
                }
                _isLoaded.value = true

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _birthdayViewModelState = false
            }
            //viewModelScope bittiğinde değerler tekrar null olmalı
            _isLoaded.value = null
            _birthdayViewModelState = null
        }
    }


    fun reSaveDeletedBirthday(birthdayId: String, birthday: Birthday) {
        viewModelScope.launch {
            try {
                repository.reSaveDeletedBirthday(birthdayId, birthday) { isSuccess ->
                    _birthdayViewModelState = isSuccess
                }
                delay(3000)
                _isLoaded.value = true

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _birthdayViewModelState = false
            }
            //viewModelScope bittiğinde değerler tekrar null olmalı
            _isLoaded.value = null
            _birthdayViewModelState = null
        }
    }




    fun deleteBirthdayPermanently(birthdayId: String) {

        viewModelScope.launch {
            try {
                repository.deleteBirthdayPermanently(birthdayId) { isSuccess ->
                    _birthdayViewModelState = isSuccess
                }

                delay(3000)
                _isLoaded.value = true

            } catch (e: Exception) {
                Log.e("HATA", "$e")
                _birthdayViewModelState = false
            }

            _isLoaded.value = null
            _birthdayViewModelState = null
        }
    }

*/

    @SuppressLint("CheckResult")
    fun sortBirthdaysMainPage(sort: String): List<Birthday> {
        val monthMap = mapOf(
            "January" to 1, "February" to 2, "March" to 3, "April" to 4,
            "May" to 5, "June" to 6, "July" to 7, "August" to 8,
            "September" to 9, "October" to 10, "November" to 11, "December" to 12
        )

        return when (sort) {
            "sortBirthdaysByNameAsc" -> birthdayList.value?.sortedBy { it.name } ?: emptyList()
            "sortBirthdaysByNameDsc" -> birthdayList.value?.sortedByDescending { it.name } ?: emptyList()
            "sortBirthdaysByBirthdayDateAsc" -> birthdayList.value?.sortedWith(compareBy(
                { val parts = it.birthdayDate.split(" ")
                    monthMap[parts[1]] },
                { val parts = it.birthdayDate.split(" ")
                    parts[0].toInt() }
            )) ?: emptyList()
            "sortBirthdaysByBirthdayDateDsc" -> birthdayList.value?.sortedWith(compareBy(
                { val parts = it.birthdayDate.split(" ")
                    monthMap[parts[1]] },
                { val parts = it.birthdayDate.split(" ")
                    parts[0].toInt() }
            ))?.reversed() ?: emptyList()
            "sortBirthdaysByRecordedDateAsc" -> birthdayList.value?.sortedBy { it.recordedDate } ?: emptyList()
            "sortBirthdaysByRecordedDateDsc" -> birthdayList.value?.sortedByDescending { it.recordedDate } ?: emptyList()
            else -> emptyList()
        }
    }

    fun sortBirthdaysTrashBin(sort: String): List<Birthday> {
        val monthMap = mapOf(
            "January" to 1, "February" to 2, "March" to 3, "April" to 4,
            "May" to 5, "June" to 6, "July" to 7, "August" to 8,
            "September" to 9, "October" to 10, "November" to 11, "December" to 12
        )

        return when(sort) {
            "sortBirthdaysByNameAsc" -> deletedBirthdayList.value?.sortedBy { it.name } ?: emptyList()
            "sortBirthdaysByNameDsc" -> deletedBirthdayList.value?.sortedByDescending { it.name } ?: emptyList()
            "sortBirthdaysByBirthdayDateAsc" -> deletedBirthdayList.value?.sortedWith(compareBy(
                { val parts = it.birthdayDate.split(" ")
                    monthMap[parts[1]] },
                { val parts = it.birthdayDate.split(" ")
                    parts[0].toInt() }
            )) ?: emptyList()
            "sortBirthdaysByBirthdayDateDsc" -> deletedBirthdayList.value?.sortedWith(compareBy(
                { val parts = it.birthdayDate.split(" ")
                    monthMap[parts[1]] },
                { val parts = it.birthdayDate.split(" ")
                    parts[0].toInt() }
            ))?.reversed() ?: emptyList()
            "sortBirthdaysByRecordedDateAsc" -> deletedBirthdayList.value?.sortedBy { it.recordedDate } ?: emptyList()
            "sortBirthdaysByRecordedDateDsc" -> deletedBirthdayList.value?.sortedByDescending { it.recordedDate } ?: emptyList()
            else -> emptyList()
        }
    }


}
