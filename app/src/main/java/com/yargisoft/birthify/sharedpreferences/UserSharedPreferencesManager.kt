package com.yargisoft.birthify.sharedpreferences

import android.content.Context

class UserSharedPreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences(UserConstants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSession(email: String, userId: String, isChecked: Boolean) {
        with(preferences.edit()) {
            putString(UserConstants.PREF_EMAIL, email)
            putString(UserConstants.KEY_USER_ID, userId)
            putBoolean(UserConstants.PREF_CHECK, isChecked)
            apply()
        }
    }

    fun saveIsChecked(isChecked:Boolean){
        with(preferences.edit()) {
            putBoolean(UserConstants.PREF_CHECK, isChecked)
            apply()
        }
    }

    fun getUserCredentials(): Pair<String?, String?> {
        val email = preferences.getString(UserConstants.PREF_EMAIL, null)
        val userId = preferences.getString(UserConstants.KEY_USER_ID, null)
        return Pair(email, userId)
    }


    fun getUserId(): String {
        return preferences.getString(UserConstants.KEY_USER_ID, "").toString()
    }
    fun checkIsUserLoggedIn(): Boolean {
        return preferences.getBoolean(UserConstants.PREF_CHECK, false)
    }
    fun clearUserSession() {
        with(preferences.edit()) {
            remove(UserConstants.PREF_EMAIL)
            remove(UserConstants.KEY_USER_ID)
            remove(UserConstants.PREF_CHECK)
            apply()
        }
    }
    fun saveAsGuest(){
        with(preferences.edit()) {
            putString(UserConstants.PREF_EMAIL, "guest")
            putString(UserConstants.KEY_USER_ID, "guest")
            putBoolean(UserConstants.PREF_CHECK, true)
            apply()
        }
    }
}
