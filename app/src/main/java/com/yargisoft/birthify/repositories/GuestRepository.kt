package com.yargisoft.birthify.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yargisoft.birthify.models.Birthday
import java.time.LocalDate
import java.time.Month
import java.util.Locale
import javax.inject.Inject

class GuestRepository @Inject constructor(
    private val gson : Gson,
    private val sharedPreferences: SharedPreferences,
){


    private fun <T> SharedPreferences.putList(key: String, list: List<T>) {
        edit().putString(key, gson.toJson(list)).apply()
    }

    // Doğum günlerini kaydetme fonksiyonu
    fun saveBirthday(newBirthday: Birthday) {
        val birthdays = getBirthdays().toMutableList()
        birthdays.add(newBirthday)
        saveBirthdays(birthdays)
    }

    //doğum gününü silme fonksiyonu
    fun deleteBirthday(birthdayId: String) {
        val birthdays = getBirthdays().toMutableList()
        val deletedBirthdays = getDeletedBirthdays().toMutableList()

        val birthdayToDelete = birthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            birthdays.remove(birthdayToDelete)
            deletedBirthdays.add(birthdayToDelete)
            saveBirthdays(birthdays)
            saveDeletedBirthdays(deletedBirthdays)
        }

    }
    //doğum gününü silme fonksiyonu
    private fun removeBirthdayFromBirthdayList(birthdayId: String) {
        val birthdays = getBirthdays().toMutableList()
        val birthdayToDelete = birthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            birthdays.remove(birthdayToDelete)
            saveBirthdays(birthdays)
        }

    }


    //doğum gününü update etme fonksiyonu
    fun updateBirthday(updatedBirthday: Birthday) {
        val birthdays = getBirthdays().toMutableList()
        val index = birthdays.indexOfFirst { it.id == updatedBirthday.id }

        if (index != -1) {
            birthdays[index] = updatedBirthday
            sharedPreferences.putList("birthdays", birthdays)
        }
    }



    // Doğum günlerini kaydetme işlemi
    private fun saveBirthdays(birthdays: List<Birthday>) {
        val editor = sharedPreferences.edit()
        editor.putString("birthdays", gson.toJson(birthdays))
        editor.apply()

    }

    // Silinen doğum günlerini deleted_birthdays'e kaydetme işlemi
    private fun saveDeletedBirthdays(deletedBirthdays: List<Birthday>) {
        val editor = sharedPreferences.edit()
        editor.putString("deleted_birthdays", gson.toJson(deletedBirthdays))
        editor.apply()
    }

    // Geçmiş doğum günlerini kaydetme işlemi
    private fun savePastBirthday(birthday: Birthday) {
        val pastBirthdaysJson = sharedPreferences.getString("past_birthdays", null)
        val pastBirthdays = if (pastBirthdaysJson != null) {
            gson.fromJson(pastBirthdaysJson, Array<Birthday>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        pastBirthdays.add(birthday)

        val editor = sharedPreferences.edit()
        editor.putString("past_birthdays", gson.toJson(pastBirthdays))
        editor.apply()
    }






    // Silinen doğum gününü tekrar kaydetme fonksiyonu (local ve firebase'de)
    fun reSaveDeletedBirthday(birthdayId: String) {
        val deletedBirthdays = getDeletedBirthdays().toMutableList()
        val birthdays = getBirthdays().toMutableList()
        val birthdayToReSave = deletedBirthdays.find { it.id == birthdayId }
        if (birthdayToReSave != null) {
            deletedBirthdays.remove(birthdayToReSave)
            birthdays.add(birthdayToReSave)
            saveBirthdays(birthdays)
            saveDeletedBirthdays(deletedBirthdays)
        }




    }
    // Silinen doğum gününü kalıcı olarak silme fonksiyonu (local ve firebase'de)
    fun permanentlyDeleteBirthday(birthdayId: String) {
        val deletedBirthdays = getDeletedBirthdays().toMutableList()
        val birthdayToDelete = deletedBirthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            deletedBirthdays.remove(birthdayToDelete)
            saveDeletedBirthdays(deletedBirthdays)
        }
    }




    //geçmiş doğum günlerini ve yaklaşan doğum günlerini ayıran fonksiyon
// Geçmiş doğum günlerini ve yaklaşan doğum günlerini ayıran fonksiyon
    fun filterPastAndUpcomingBirthdays(birthdays: List<Birthday>) {
        val currentDate = LocalDate.now()
        val pastBirthdays = birthdays.filter {
            val parts = it.birthdayDate.split(" ")
            val day = parts[0].toInt()
            val month = Month.valueOf(parts[1].uppercase(Locale.ENGLISH))
            val birthdayDate = LocalDate.of(currentDate.year, month, day)
            birthdayDate.isBefore(currentDate)
        }
        pastBirthdays.forEach { pastBirthday ->
            savePastBirthday(pastBirthday)
            // Geçmiş doğum günlerini tek tek local listeden kaldırıyoruz (yukarıda past_birthdays olarak halihazırda kaydediyoruz)
            removeBirthdayFromBirthdayList(pastBirthday.id)
        }
    }


    // Doğum günlerini lokalden getirme fonksiyonları
    fun getBirthdays(): List<Birthday> {
        val json = sharedPreferences.getString("birthdays", null)
        return if (json != null) {
            val type = object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    fun getDeletedBirthdays(): List<Birthday> {
        val json = sharedPreferences.getString("deleted_birthdays", null)
        return if (json != null) {
            val type= object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    fun getPastBirthdays(): List<Birthday> {
        val json = sharedPreferences.getString("past_birthdays", null)
        return if (json != null) {
            val type = object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }


    //Bu fonksiyonlar kullanıcı hesabını "tamamen"!!! sildiğinde çalıştırılacak
    // Doğum günlerini temizleme fonksiyonu
    private fun clearBirthdays() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
    // deleted_birthdays listesini tamamen temizleme fonksiyonu
    private fun clearDeletedBirthdays() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

    }
    // past_birthdays listesini tamamen temizleme fonksiyonu
    private fun clearPastBirthdays() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun clearAllBirthdays(){
        clearPastBirthdays()
        clearBirthdays()
        clearDeletedBirthdays()
    }




}