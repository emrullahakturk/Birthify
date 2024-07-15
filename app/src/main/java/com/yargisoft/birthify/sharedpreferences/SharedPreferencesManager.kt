package com.yargisoft.birthify.sharedpreferences

import android.content.Context

class SharedPreferencesManager(private val context: Context) {
    private val preferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSession(email: String, userId: String, isChecked: Boolean) {
        with(preferences.edit()) {
            putString(Constants.PREF_EMAIL, email)
            putString(Constants.KEY_USER_ID, userId)
            putBoolean(Constants.PREF_CHECK, isChecked)
            apply()
        }
    }

    fun getUserCredentials(): Pair<String?, String?> {
        val email = preferences.getString(Constants.PREF_EMAIL, null)
        val userId = preferences.getString(Constants.KEY_USER_ID, null)
        return Pair(email, userId)
    }

    fun getUserId(): String {
        return preferences.getString(Constants.KEY_USER_ID, "").toString()
    }
    fun checkIsUserLoggedIn(): Boolean{
        val isChecked = preferences.getBoolean(Constants.PREF_CHECK,false)
        return isChecked
    }
    fun clearUserSession() {
        with(preferences.edit()) {
            remove(Constants.PREF_EMAIL)
            remove(Constants.KEY_USER_ID)
            remove(Constants.PREF_CHECK)
            apply()
        }
    }
}
