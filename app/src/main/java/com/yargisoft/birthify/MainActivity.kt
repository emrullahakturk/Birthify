package com.yargisoft.birthify

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var networkConnectionObserver: NetworkConnectionObserver
    private lateinit var userSharedPreferences: UserSharedPreferencesManager


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





        userSharedPreferences = UserSharedPreferencesManager(this)


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

        networkConnectionObserver = NetworkConnectionObserver(this)

        networkConnectionObserver.isConnected.observe(this) { isConnected ->
            if(userSharedPreferences.getUserCredentials().second != "guest"){
                if (!isConnected) {
                    showNetworkAlertDialog()
                }
            }
        }





    }

    private fun updateLocale(context: Context, localeCode: String): Context {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }


    private fun showNetworkAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Connection Error")
            .setMessage("Your internet connection is lost. Your birthdays will not be saved in the database without an internet connection. Please check your internet connection..")
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }


}
