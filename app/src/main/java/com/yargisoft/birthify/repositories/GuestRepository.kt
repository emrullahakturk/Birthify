package com.yargisoft.birthify.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yargisoft.birthify.models.Birthday
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale

class GuestRepository(context: Context) {

    private val gson = Gson()
    private val birthdayPreferences: SharedPreferences = context.getSharedPreferences("birthdays", Context.MODE_PRIVATE)
    private val deletedBirthdayPreferences: SharedPreferences = context.getSharedPreferences("deleted_birthdays", Context.MODE_PRIVATE)
    private val pastBirthdayPreferences: SharedPreferences = context.getSharedPreferences("past_birthdays", Context.MODE_PRIVATE)


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
            birthdayPreferences.putList("birthdays", birthdays)
        }
    }



    // Doğum günlerini kaydetme işlemi
    private fun saveBirthdays(birthdays: List<Birthday>) {
        val editor = birthdayPreferences.edit()
        editor.putString("birthdays", gson.toJson(birthdays))
        editor.apply()

    }

    // Silinen doğum günlerini deleted_birthdays'e kaydetme işlemi
    private fun saveDeletedBirthdays(deletedBirthdays: List<Birthday>) {
        val editor = deletedBirthdayPreferences.edit()
        editor.putString("deleted_birthdays", gson.toJson(deletedBirthdays))
        editor.apply()
    }

    // Geçmiş doğum günlerini kaydetme işlemi
    private fun savePastBirthday(birthday: Birthday) {
        val pastBirthdaysJson = pastBirthdayPreferences.getString("past_birthdays", null)
        val pastBirthdays = if (pastBirthdaysJson != null) {
            gson.fromJson(pastBirthdaysJson, Array<Birthday>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        pastBirthdays.add(birthday)

        val editor = pastBirthdayPreferences.edit()
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
        val json = birthdayPreferences.getString("birthdays", null)
        return if (json != null) {
            val type = object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    fun getDeletedBirthdays(): List<Birthday> {
        val json = deletedBirthdayPreferences.getString("deleted_birthdays", null)
        return if (json != null) {
            val type= object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    fun getPastBirthdays(): List<Birthday> {
        val json = pastBirthdayPreferences.getString("past_birthdays", null)
        return if (json != null) {
            val type = object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }


    //Bu fonksiyonlar kullanıcı hesabını "tamamen"!!! sildiğinde çalıştırılacak
    // Doğum günlerini temizleme fonksiyonu
    fun clearBirthdays() {
        val editor = birthdayPreferences.edit()
        editor.clear()
        editor.apply()
    }
    // deleted_birthdays listesini tamamen temizleme fonksiyonu
    fun clearDeletedBirthdays() {
        val editor = deletedBirthdayPreferences.edit()
        editor.clear()
        editor.apply()

    }
    // past_birthdays listesini tamamen temizleme fonksiyonu
    fun clearPastBirthdays() {
        val editor = pastBirthdayPreferences.edit()
        editor.clear()
        editor.apply()
    }


}