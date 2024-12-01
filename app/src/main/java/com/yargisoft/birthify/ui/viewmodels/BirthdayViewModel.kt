package com.yargisoft.birthify.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BirthdayViewModel @Inject constructor(private val repository: BirthdayRepository) : ViewModel() {


    private val _birthdayList = MutableLiveData<List<Birthday>>()
    val birthdayList: LiveData<List<Birthday>> get() = _birthdayList

    private val _isLoaded = MutableStateFlow<Boolean?>(null)
    val isLoaded: StateFlow<Boolean?> get() = _isLoaded


    private var _birthdayViewModelState :Boolean? = null
    val birthdayViewModelState :Boolean? get() = _birthdayViewModelState

    private val monthMap = mapOf(
        "January" to 1, "February" to 2, "March" to 3, "April" to 4,
        "May" to 5, "June" to 6, "July" to 7, "August" to 8,
        "September" to 9, "October" to 10, "November" to 11, "December" to 12
    )

    fun saveBirthday(birthday: Birthday){
        repository.saveBirthdaytoPreferences(birthday,"birthdays")
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

    fun clearAllBirthdays(){
        repository.clearAllBirthdays()
    }



    //shared preferences ile kaydettiğimiz fonksiyonları listelere aktarıyoruz
    fun getBirthdays(keyString: String){
        _birthdayList.value = repository.getBirthdaysFromPreferences(keyString)
        if(keyString =="past_birthdays"){
            _birthdayList.value = sortBirthdays("sortBirthdaysByBirthdayDateDsc",_birthdayList.value ?: emptyList())
        }
    }

    //firebase üzerinden doğum günlerini çekerek preferences'a ve burada tanımladığımız listelere kaydediyoruz
    fun getBirthdaysFromFirebase(userId: String, keyString: String) {
        viewModelScope.launch {
            try {
                repository.getBirthdaysFromFirebase(keyString,userId) { birthdays, isSuccess ->
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



    fun sortBirthdays(sort: String, sortList: List<Birthday>): List<Birthday> {
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

}
