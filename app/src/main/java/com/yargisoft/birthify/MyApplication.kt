package com.yargisoft.birthify

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Temayı ayarla
        setAppTheme()
    }

    private fun setAppTheme() {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_theme_enabled", false)

        // Uygulama seviyesinde tema ayarı
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}