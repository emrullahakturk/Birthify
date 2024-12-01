package com.yargisoft.birthify.utils.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class UserSharedPreferencesManager @Inject constructor(
    @ApplicationContext val context: Context,
    private val preferences: SharedPreferences
) {

    companion object {
        private const val DARK_THEME_KEY = "dark_theme_enabled"
    }

    // Karanlık tema ayarını kontrol eden fonksiyon
    fun isDarkThemeEnabled(): Boolean {
        return preferences.getBoolean(DARK_THEME_KEY, false)
    }

    // Karanlık tema ayarını değiştiren fonksiyon
    fun setDarkThemeToggle(isEnabled: Boolean) {
        with(preferences.edit()) {
            putBoolean(DARK_THEME_KEY, isEnabled)
            apply()
        }
    }

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
