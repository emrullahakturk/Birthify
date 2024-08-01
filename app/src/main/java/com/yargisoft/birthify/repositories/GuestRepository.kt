package com.yargisoft.birthify.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yargisoft.birthify.models.Birthday

class GuestRepository(context: Context) {

    private val gson = Gson()
    private val birthdayPreferences: SharedPreferences = context.getSharedPreferences("birthdays", Context.MODE_PRIVATE)
    private val deletedBirthdayPreferences: SharedPreferences = context.getSharedPreferences("deleted_birthdays", Context.MODE_PRIVATE)


    private fun <T> SharedPreferences.putList(key: String, list: List<T>) {
        edit().putString(key, gson.toJson(list)).apply()
    }



    // Doğum günlerini kaydetme fonksiyonu
    fun saveBirthday(newBirthday: Birthday) {
        val birthdays = getBirthdays().toMutableList()
        birthdays.add(newBirthday)
        saveBirthdays(birthdays)
        Log.e("tagımıs","${getBirthdays()}")
    }

    // Doğum günlerini getirme fonksiyonu
    fun getBirthdays(): List<Birthday> {
        val json = birthdayPreferences.getString("birthdays", null)
        return if (json != null) {
            val type = object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Doğum günlerini kaydetme işlemi
    private fun saveBirthdays(birthdays: List<Birthday>) {
        val editor = birthdayPreferences.edit()
        editor.putString("birthdays", gson.toJson(birthdays))
        editor.apply()
    }

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


    fun updateBirthday(updatedBirthday: Birthday) {
        val birthdays = getBirthdays().toMutableList()
        val index = birthdays.indexOfFirst { it.id == updatedBirthday.id }

        if (index != -1) {
            birthdays[index] = updatedBirthday
            birthdayPreferences.putList("birthdays", birthdays)
        }
    }

    // Doğum günlerini temizleme fonksiyonu
    fun clearBirthdays() {
        val editor = birthdayPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // Silinen doğum günlerini kaydetme işlemi
    private fun saveDeletedBirthdays(deletedBirthdays: List<Birthday>) {
        val editor = deletedBirthdayPreferences.edit()
        editor.putString("deleted_birthdays", gson.toJson(deletedBirthdays))
        editor.apply()
    }

    // Silinen doğum günlerini getirme fonksiyonu
    fun getDeletedBirthdays(): List<Birthday> {
        val json = deletedBirthdayPreferences.getString("deleted_birthdays", null)
        return if (json != null) {
            val type= object : TypeToken<List<Birthday>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Silinen doğum günlerini temizleme fonksiyonu
    fun clearDeletedBirthdays() {
        val editor = deletedBirthdayPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // Silinen doğum gününü tekrar kaydetme fonksiyonu
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

    // Silinen doğum gününü kalıcı olarak silme fonksiyonu
    fun permanentlyDeleteBirthday(birthdayId: String) {
        val deletedBirthdays = getDeletedBirthdays().toMutableList()
        val birthdayToDelete = deletedBirthdays.find { it.id == birthdayId }
        if (birthdayToDelete != null) {
            deletedBirthdays.remove(birthdayToDelete)
            saveDeletedBirthdays(deletedBirthdays)
        }
    }



}