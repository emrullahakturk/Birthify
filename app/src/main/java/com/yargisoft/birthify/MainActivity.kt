package com.yargisoft.birthify

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import java.util.Locale

class MainActivity : AppCompatActivity() {


    companion object {
        private const val PREFS_NAME = "AppSettings"
        private const val LANGUAGE_KEY = "AppLanguage"
    }

    override fun attachBaseContext(newBase: Context) {
        val preferences: SharedPreferences = newBase.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, Locale.getDefault().language) ?: Locale.getDefault().language
        val localeUpdatedContext = updateLocale(newBase, language)
        super.attachBaseContext(localeUpdatedContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.findFragmentById(R.id.mainFragmentHost) as NavHostFragment

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri tuşuna basıldığında yapılacak işlemler
                // Örneğin, bir önceki fragment'e geri gitmek:
                val navController = findNavController(R.id.mainFragmentHost)
                if (!navController.popBackStack()) {
                    // Eğer navController ile geri gidilecek bir yer yoksa, activity'i kapat
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

    }

    private fun updateLocale(context: Context, localeCode: String): Context {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }


}
