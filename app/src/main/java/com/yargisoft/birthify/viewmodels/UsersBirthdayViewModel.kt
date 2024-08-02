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

class UsersBirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {




    private val _birthdayList = MutableLiveData<List<Birthday>>()
    val birthdayList: LiveData<List<Birthday>> get() = _birthdayList

    private val _pastBirthdayList = MutableLiveData<List<Birthday>>()
    val pastBirthdayList: LiveData<List<Birthday>> get() = _pastBirthdayList

    private val _deletedBirthdayList = MutableLiveData<List<Birthday>>()
    val deletedBirthdayList: LiveData<List<Birthday>> get() = _deletedBirthdayList


    private val _isLoaded = MutableStateFlow<Boolean?>(null)
    val isLoaded: StateFlow<Boolean?> get() = _isLoaded


    private var _birthdayViewModelState :Boolean? = null
    val birthdayViewModelState :Boolean? get() = _birthdayViewModelState




    fun saveBirthday(birthday: Birthday){
        repository.saveBirthday(birthday)
    }
    fun deleteBirthday(birthdayId:String){
        repository.deleteBirthday(birthdayId )
    }
    fun updateBirthday(birthday: Birthday){
        repository.updateBirthday(birthday)
    }

    fun permanentlyDeleteBirthday(birthdayId: String){
        repository.permanentlyDeleteBirthday(birthdayId )
    }
    fun reSaveDeletedBirthday(birthdayId: String){
        repository.reSaveDeletedBirthday(birthdayId)
    }

    fun filterPastAndUpcomingBirthdays(birthdays:List<Birthday>){
        repository.filterPastAndUpcomingBirthdays(birthdays)
    }



    //Kullanıcı hesabını sildiğinde çalıştırılacak fonksiyonlar (lokalden tüm doğum günlerini siler)
    fun clearDeletedBirthdays(){
        repository.clearDeletedBirthdays()
    }
    fun clearBirthdays(){
        repository.clearBirthdays()
    }
    fun clearPastBirthdays(){
        repository.clearPastBirthdays()
    }

    //Kullanıcı hesabını sildiğinde çalıştırılacak fonksiyonlar (Firebaseden tüm doğum günlerini siler)
    fun clearDeletedBirthdaysFirebase(){
        repository.clearDeletedBirthdaysFirebase()
    }
    fun clearBirthdaysFirebase(){
        repository.clearBirthdaysFirebase()
    }
    fun clearPastBirthdaysFirebase(){
        repository.clearPastBirthdaysFirebase()
    }



    //shared preferences ile kaydettiğimiz fonksiyonları listelere aktarıyoruz
    fun getDeletedBirthdays(){
        _deletedBirthdayList.value =  repository.getDeletedBirthdays()
    }
    fun getBirthdays(){
        _birthdayList.value =  repository.getBirthdays()
    }
    fun getPastBirthdays(){
        _pastBirthdayList.value =  repository.getPastBirthdays()
    }


    //firebase üzerinden doğum günlerini çekerek preferences'a ve burada tanımladığımız listelere kaydediyoruz
    fun getUserBirthdaysFromFirebase(userId: String) {
        viewModelScope.launch {
            try {
                repository.getUserBirthdaysFromFirebase(userId) { birthdays, isSuccess ->
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
    fun getDeletedBirthdaysFromFirebase(userId: String) {
        viewModelScope.launch {
            try {
                repository.getDeletedBirthdaysFromFirebase(userId) { birthdays, isSuccess ->

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
    fun getPastBirthdaysFromFirebase(userId: String) {
        viewModelScope.launch {
            try {
                repository.getPastBirthdaysFromFirebase(userId) { birthdays, isSuccess ->

                    _pastBirthdayList.value = birthdays.sortedByDescending { it.recordedDate }
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

    fun sortBirthdaysPastBirthdays(sort: String): List<Birthday> {
        val monthMap = mapOf(
            "January" to 1, "February" to 2, "March" to 3, "April" to 4,
            "May" to 5, "June" to 6, "July" to 7, "August" to 8,
            "September" to 9, "October" to 10, "November" to 11, "December" to 12
        )

        return when(sort) {
            "sortBirthdaysByNameAsc" -> pastBirthdayList.value?.sortedBy { it.name } ?: emptyList()
            "sortBirthdaysByNameDsc" -> pastBirthdayList.value?.sortedByDescending { it.name } ?: emptyList()
            "sortBirthdaysByBirthdayDateAsc" -> pastBirthdayList.value?.sortedWith(compareBy(
                { val parts = it.birthdayDate.split(" ")
                    monthMap[parts[1]] },
                { val parts = it.birthdayDate.split(" ")
                    parts[0].toInt() }
            )) ?: emptyList()
            "sortBirthdaysByBirthdayDateDsc" -> pastBirthdayList.value?.sortedWith(compareBy(
                { val parts = it.birthdayDate.split(" ")
                    monthMap[parts[1]] },
                { val parts = it.birthdayDate.split(" ")
                    parts[0].toInt() }
            ))?.reversed() ?: emptyList()
            "sortBirthdaysByRecordedDateAsc" -> pastBirthdayList.value?.sortedBy { it.recordedDate } ?: emptyList()
            "sortBirthdaysByRecordedDateDsc" -> pastBirthdayList.value?.sortedByDescending { it.recordedDate } ?: emptyList()
            else -> emptyList()
        }
    }


}
