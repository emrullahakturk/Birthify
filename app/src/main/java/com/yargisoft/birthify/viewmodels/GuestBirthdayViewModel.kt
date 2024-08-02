package com.yargisoft.birthify.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.GuestRepository

class GuestBirthdayViewModel(val repository: GuestRepository): ViewModel() {

    private val _birthdayList = MutableLiveData<List<Birthday>>()
    val birthdayList: LiveData<List<Birthday>> get() = _birthdayList


    private val _pastBirthdayList = MutableLiveData<List<Birthday>>()
    val pastBirthdayList: LiveData<List<Birthday>> get() = _pastBirthdayList

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

    fun permanentlyDeleteBirthday(birthdayId: String){
            repository.permanentlyDeleteBirthday(birthdayId )
    }

    fun reSaveDeletedBirthday(birthdayId: String){
            repository.reSaveDeletedBirthday(birthdayId)
    }




    fun getBirthdays(){
        _birthdayList.value =  repository.getBirthdays()
    }
    fun getDeletedBirthdays(){
        _deletedBirthdayList.value =  repository.getDeletedBirthdays()
    }
    fun getPastBirthdays(){
        _pastBirthdayList.value =  repository.getPastBirthdays()
    }



    fun clearPastBirthdays(){
        repository.clearPastBirthdays()
    }
    fun clearDeletedBirthdays(){
        repository.clearDeletedBirthdays()
    }
    fun clearBirthdays(){
        repository.clearBirthdays()
    }



//Sort Fonksiyonları

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