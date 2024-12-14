package com.yargisoft.birthify.utils.sharedpreferences

import android.content.Context
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.EMAIL_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_USER
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.USER_ID_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.USER_TOKEN_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class UserSharedPreferencesManager @Inject constructor(
    @ApplicationContext val context: Context,
) {

    private val preferences = context.getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE)

    fun getUserCredentials(): Triple<String?, String?, String?> {
        val email = preferences.getString(EMAIL_KEY, null)
        val userId = preferences.getString(USER_ID_KEY, null)
        val token = preferences.getString(USER_TOKEN_KEY, null)
        return Triple(email, userId, token)
    }


    fun getUserId(): String {
        return preferences.getString(USER_ID_KEY, "").toString()
    }

    fun getToken(): String? {
        return preferences.getString(USER_TOKEN_KEY, null)
    }


    fun clearUserSession() {
        with(preferences.edit()) {
            remove(EMAIL_KEY)
            remove(USER_ID_KEY)
            apply()
        }
    }

}
