package com.yargisoft.birthify.sharedpreferences

import android.content.Context
import android.content.SharedPreferences

class UserSharedPreferencesManager(context: Context) {
    val preferences: SharedPreferences = context.getSharedPreferences(UserConstants.PREFS_NAME, Context.MODE_PRIVATE)


    fun getUserCredentials(): Triple<String?, String?, String?> {
        val email = preferences.getString(UserConstants.PREF_EMAIL, null)
        val userId = preferences.getString(UserConstants.KEY_USER_ID, null)
        val token = preferences.getString(UserConstants.USER_TOKEN, null)
        return Triple(email, userId, token)
    }


    fun getUserId(): String {
        return preferences.getString(UserConstants.KEY_USER_ID, "").toString()
    }

    fun getToken(): String? {
        return preferences.getString("token", null)
    }


    fun clearUserSession() {
        with(preferences.edit()) {
            remove(UserConstants.PREF_EMAIL)
            remove(UserConstants.KEY_USER_ID)
            apply()
        }
    }
    fun saveAsGuest(){
        with(preferences.edit()) {
            putString(UserConstants.PREF_EMAIL, "guest")
            putString(UserConstants.KEY_USER_ID, "guest")
            putString(UserConstants.USER_TOKEN, "guest")
            apply()
        }
    }
}
