package com.yargisoft.birthify

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import com.yargisoft.birthify.databinding.ActivityAuthBinding
import com.yargisoft.birthify.utils.NetworkConnectionObserver
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.LANGUAGE_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_SETTINGS
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {


    @Inject
    lateinit var networkConnectionObserver: NetworkConnectionObserver

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var fragmentContainerView: FragmentContainerView
    private lateinit var binding: ActivityAuthBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)

        fragmentContainerView = binding.navHostFragment
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment


        networkConnectionObserver.isConnected.observe(this) { isConnected ->
            if (!isConnected) {
                showNetworkAlertDialog()
            }
        }



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
}