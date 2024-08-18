package com.yargisoft.birthify.views.guest_user_views

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.MainActivity
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.databinding.FragmentAuthSettingsBinding
import com.yargisoft.birthify.databinding.FragmentGuestSettingsBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.GuestViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory
import java.util.Locale

class GuestSettingsFragment : Fragment() {

    private lateinit var binding: FragmentGuestSettingsBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var guestBirthdayViewModel: GuestBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var authViewModelFactory: AuthViewModelFactory

    companion object{
        private const val PREFS_NAME = "AppSettings"
        private const val LANGUAGE_KEY = "AppLanguage"
    }




    // İzin isteme işlemini başlatmak için launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("NotificationPermission", "Bildirim izni verildi.")
        } else {
            Log.d("NotificationPermission", "Bildirim izni reddedildi.")
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_guest_settings, container, false)

        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        val lottieAnimationView = binding.threePointAnimation


        authRepository = AuthRepository(userSharedPreferences.preferences)
        authViewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authViewModelFactory)[AuthViewModel::class]

        val guestBirthdayRepository = GuestRepository(requireContext())
        val guestBirthdayFactory = GuestViewModelFactory(guestBirthdayRepository)
        guestBirthdayViewModel = ViewModelProvider(this, guestBirthdayFactory)[GuestBirthdayViewModel::class]

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbarGuestSettings.findViewById<View>(R.id.menuButtonToolbar)


        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            userSharedPreferences,
            "GuestSettings"
        )

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.guestSettingsToMain)
                    true
                }
                R.id.bottomNavTrashBin -> {
                    findNavController().navigate(R.id.guestSettingsToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.guestSettingsToPastBirthdays)
                    true
                }
                else -> false
            }
        }
        binding.deleteAllBirthdaysCardView.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirm Deletion All Birthdays")
                .setMessage("Are you sure you want to delete your birthdays? This operation cannot be undone.")
                .setPositiveButton("Yes") { _, _ ->
                    guestBirthdayViewModel.clearAllBirthdays()
                    findNavController().navigate(R.id.guestSettingsToMain)
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }




        // Karanlık tema ayarını kontrol et ve Switch'i ayarla
        val isDarkThemeEnabled = userSharedPreferences.isDarkThemeEnabled()
        binding.darkThemeSwitch.isChecked = isDarkThemeEnabled

        // Bildirim Switch'ini yönet
        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableNotifications()
            } else {
                disableNotifications()
            }
        }



        // Karanlık Tema Switch'ini yönet
        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            userSharedPreferences.setDarkThemeEnabled(isChecked)
            if (isChecked) {
                enableDarkTheme()
            } else {
                disableDarkTheme()
            }
        }

        val currentLanguage = getCurrentLanguage()
        binding.languageSelectImageView.setOnClickListener {
            showLanguageSelectionDialog(currentLanguage)
        }



        return binding.root
    }

    private fun getCurrentLanguage(): String {
        val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return preferences.getString(LANGUAGE_KEY, Locale.getDefault().language) ?: Locale.getDefault().language
    }

    private fun showLanguageSelectionDialog(currentLanguage: String) {
        val languages = arrayOf("English", "Türkçe") // Dil seçenekleri
        val languageCodes = arrayOf("en", "tr") // Dil kodları

        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val selectedLanguage = languageCodes[which]
                if (selectedLanguage != currentLanguage) {
                    setLocale(selectedLanguage)
                }
            }
            .show()
    }

    private fun setLocale(languageCode: String) {
        val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(LANGUAGE_KEY, languageCode).apply()

        // Uygulamanın dilini değiştir ve Activity'yi yeniden başlat
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun enableDarkTheme() {
        // Karanlık temayı etkinleştir
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun disableDarkTheme() {
        // Karanlık temayı devre dışı bırak
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun enableNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Tiramisu ve sonrasında bildirim izni iste
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Tiramisu öncesi doğrudan bildirimleri etkinleştir
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                startActivity(intent)
            }
        }
    }

    private fun disableNotifications() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        startActivity(intent)
    }




    override fun onResume() {
        super.onResume()

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isNotificationEnabled = notificationManager.areNotificationsEnabled()
        binding.notificationSwitch.isChecked = isNotificationEnabled


        val isDarkThemeEnabled = userSharedPreferences.isDarkThemeEnabled()
        binding.darkThemeSwitch.isChecked = isDarkThemeEnabled
    }

}