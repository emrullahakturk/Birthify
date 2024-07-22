package com.yargisoft.birthify.sharedpreferences

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.yargisoft.birthify.models.Birthday

class BirthdaySharedPreferencesManager(private val context:Context) {
    private val preferences = context.getSharedPreferences("birthdays", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveBirthday(birthdays: List<Birthday>) {
        val editor = preferences.edit()
        editor.putString("birthdays", gson.toJson(birthdays))
        editor.apply()
    }

    fun getBirthdays(): List<Birthday> {
        val birthdaysJson = preferences.getString("birthdays", "[]")
        val type = object : TypeToken<List<Birthday>>() {}.type
        return gson.fromJson(birthdaysJson, type)
    }

    fun clearBirthdays() {
        val editor = preferences.edit()
        editor.remove("birthdays")
        editor.apply()
    }





}