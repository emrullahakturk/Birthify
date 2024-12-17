package com.yargisoft.birthify.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.sharedpreferences.UserConstants.LANGUAGE_KEY
import com.yargisoft.birthify.data.sharedpreferences.UserConstants.PREFS_SETTINGS
import com.yargisoft.birthify.data.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.databinding.ActivityMainBinding
import com.yargisoft.birthify.utils.helpers.NetworkConnectionObserver
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var networkConnectionObserver: NetworkConnectionObserver

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationRailView: NavigationRailView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private lateinit var navController: NavController

    override fun attachBaseContext(newBase: Context) {
        val preferences: SharedPreferences =
            newBase.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, Locale.getDefault().language)
            ?: Locale.getDefault().language
        val localeUpdatedContext = updateLocale(newBase, language)
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkConnectionObserver.isConnected.observe(this) { isConnected ->
            if (!isConnected) {
                showNetworkAlertDialog()
            }
        }

        toolbar = binding.toolbar
        drawerLayout = binding.drawerLayout
        navigationRailView = binding.navigationRailView
        bottomNav = binding.bottomNavigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainPageFragment,
                R.id.profileFragment,
                R.id.settingsFragment,
                R.id.trashBinFragment,
                R.id.pastBirthdaysFragment,
            ),
            drawerLayout
        )

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        navigationRailView.setupWithNavController(navHostFragment.navController)
        bottomNav.setupWithNavController(navHostFragment.navController)

        val logoutMenuItem = navigationRailView.menu.findItem(R.id.labelLogOut)
        logoutMenuItem.setOnMenuItemClickListener {
            logout(this)
            true
        }

        onBackPressedDispatcher.addCallback(this) {
            if (navigationRailView.visibility == View.GONE) {
                navigationRailView.visibility = View.VISIBLE
            } else {
                navHostFragment.navController.popBackStack()
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_line)

        navigationRailView.setOnItemSelectedListener { item ->
            val handled = NavigationUI.onNavDestinationSelected(item, navController)
            if (handled) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            handled
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
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun logout(activity: Activity) {
        userSharedPreferences.clearUserSession()
        val intent = Intent(activity, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
