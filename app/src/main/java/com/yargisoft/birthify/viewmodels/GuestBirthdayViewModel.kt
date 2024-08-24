package com.yargisoft.birthify.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.GuestRepository
import java.util.Locale

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




    fun getDeletedBirthdays(){
        _deletedBirthdayList.value =  repository.getDeletedBirthdays().sortedByDescending { it.recordedDate }
    }
    fun getBirthdays(){
        _birthdayList.value =  repository.getBirthdays().sortedByDescending { it.recordedDate }
    }
    fun getPastBirthdays(){
        _pastBirthdayList.value = repository.getPastBirthdays()
        _pastBirthdayList.value = sortWithPage("sortBirthdaysByBirthdayDateDsc","PastBirthdays")
    }



    /*fun clearPastBirthdays(){
        repository.clearPastBirthdays()
    }
    fun clearDeletedBirthdays(){
        repository.clearDeletedBirthdays()
    }
    fun clearBirthdays(){
        repository.clearBirthdays()
    }*/
    fun clearAllBirthdays(){
       repository.clearAllBirthdays()
    }

    fun filterPastAndUpcomingBirthdays(birthdays:List<Birthday>){
        repository.filterPastAndUpcomingBirthdays(birthdays)
    }




//Sort Fonksiyonları

    @SuppressLint("CheckResult")
    private fun sortBirthdays(sort: String, sortList: List<Birthday>): List<Birthday> {
        val monthMap = mapOf(
            "January" to 1, "February" to 2, "March" to 3, "April" to 4,
            "May" to 5, "June" to 6, "July" to 7, "August" to 8,
            "September" to 9, "October" to 10, "November" to 11, "December" to 12
        )
        return when (sort) {
            "sortBirthdaysByNameAsc" -> sortList.sortedBy { it.name.lowercase(Locale.getDefault()) }
            "sortBirthdaysByNameDsc" -> sortList.sortedByDescending { it.name.lowercase(Locale.getDefault()) }
            "sortBirthdaysByBirthdayDateAsc" -> sortList.sortedWith(compareBy(
                { val parts = it.birthdayDate.split(" ")
                    monthMap[parts[1]] },
                { val parts = it.birthdayDate.split(" ")
                    parts[0].toInt() }
            ))
            "sortBirthdaysByBirthdayDateDsc" -> sortList.sortedWith(compareBy(
                { val parts = it.birthdayDate.split(" ")
                    monthMap[parts[1]] },
                { val parts = it.birthdayDate.split(" ")
                    parts[0].toInt() }
            )).reversed()
            "sortBirthdaysByRecordedDateAsc" -> sortList.sortedBy { it.recordedDate }
            "sortBirthdaysByRecordedDateDsc" -> sortList.sortedByDescending { it.recordedDate }
            else -> emptyList()
        }
    }

    fun sortWithPage(sort: String, sourcePage : String ):List<Birthday>{
        var sortList : List<Birthday> = emptyList()
        when(sourcePage){
            "PastBirthdays" -> sortList =  _pastBirthdayList.value ?: emptyList()
            "Main" -> sortList =  _birthdayList.value ?: emptyList()
            "TrashBin" -> sortList =  _deletedBirthdayList.value ?: emptyList()
        }
        return sortBirthdays(sort,sortList)
    }



}