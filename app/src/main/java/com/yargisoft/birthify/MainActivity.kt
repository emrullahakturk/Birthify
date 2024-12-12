package com.yargisoft.birthify

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.databinding.ActivityMainBinding
import com.yargisoft.birthify.utils.NetworkConnectionObserver
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.LANGUAGE_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_NAME
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var networkConnectionObserver: NetworkConnectionObserver

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar


    override fun attachBaseContext(newBase: Context) {
        val preferences: SharedPreferences =
            newBase.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, Locale.getDefault().language)
            ?: Locale.getDefault().language
        val localeUpdatedContext = updateLocale(newBase, language)
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        networkConnectionObserver.isConnected.observe(this) { isConnected ->
            if (userSharedPreferences.getUserCredentials().second != "guest") {
                if (!isConnected) {
                    showNetworkAlertDialog()
                }
            }
        }



        toolbar = binding.toolbar
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        bottomNav = binding.bottomNavigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.labelLogOut,
                R.id.addBirthdayFragment,
                R.id.mainPageFragment,
                R.id.birthdayDetailFragment,
                R.id.birthdayEditFragment,
                R.id.deletedBirthdayDetailFragment,
                R.id.profileFragment,
                R.id.accountDetailsFragment,
                R.id.settingsFragment,
                R.id.trashBinFragment,
                R.id.pastBirthdaysFragment,
            ),
            drawerLayout
        )

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        navigationView.setupWithNavController(navHostFragment.navController)
        bottomNav.setupWithNavController(navHostFragment.navController)



        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.labelLogOut -> {
                    Toast.makeText(this, "Çıkış Yapıldı", Toast.LENGTH_SHORT).show()
                    //performLogout()
                    true
                }

                else -> {
                    true
                }
            }
        }


        onBackPressedDispatcher.addCallback(this) {
            navHostFragment.navController.popBackStack()
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
            .setTitle(getString(R.string.connection_error_title))
            .setMessage(getString(R.string.connection_error))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
