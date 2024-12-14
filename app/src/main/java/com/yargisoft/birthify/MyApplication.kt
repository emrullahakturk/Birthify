package com.yargisoft.birthify

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.DARK_THEME_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_SETTINGS
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {


    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        setAppTheme()
    }

    private fun setAppTheme() {
        val prefs = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean(DARK_THEME_KEY, false)

        Log.d("AppTheme", "Dark Mode: $isDarkMode")

        // Uygulama seviyesinde tema ayarı
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory) // Hilt Worker Factory kullanımı
            .build()

}