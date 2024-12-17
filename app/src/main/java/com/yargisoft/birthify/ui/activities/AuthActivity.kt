package com.yargisoft.birthify.ui.activities

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.sharedpreferences.UserConstants.LANGUAGE_KEY
import com.yargisoft.birthify.data.sharedpreferences.UserConstants.PREFS_SETTINGS
import com.yargisoft.birthify.databinding.ActivityAuthBinding
import com.yargisoft.birthify.utils.helpers.NetworkConnectionObserver
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var networkConnectionObserver: NetworkConnectionObserver


    private lateinit var navHostFragment: NavHostFragment
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding ile layoutu bağla
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation bileşeni
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        // Ağ bağlantısı gözlemcisi
        networkConnectionObserver.isConnected.observe(this) { isConnected ->
            if (!isConnected) {
                showNetworkAlertDialog()
            }
        }

        // Geri tuşuna basıldığında davranış
        onBackPressedDispatcher.addCallback(this) {
            navHostFragment.navController.popBackStack()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val preferences: SharedPreferences =
            newBase.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, Locale.getDefault().language)
            ?: Locale.getDefault().language
        val localeUpdatedContext = updateLocale(newBase, language)
        super.attachBaseContext(localeUpdatedContext)
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
            .setTitle(getString(R.string.connection_error_title))
            .setMessage(getString(R.string.connection_error))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
