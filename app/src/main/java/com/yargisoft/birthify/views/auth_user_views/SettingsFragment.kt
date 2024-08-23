package com.yargisoft.birthify.views.auth_user_views

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.MainActivity
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.databinding.FragmentAuthSettingsBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory
import com.yargisoft.birthify.views.dialogs.FrequentlyAskedQuestionsDialogFragment
import com.yargisoft.birthify.views.dialogs.PrivacyPolicyDialogFragment
import com.yargisoft.birthify.views.dialogs.WhatIsBirthifyDialogFragment
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentAuthSettingsBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_settings, container, false)

        userSharedPreferences = UserSharedPreferencesManager(requireContext())

       // val lottieAnimationView = binding.threePointAnimation

        authRepository = AuthRepository(userSharedPreferences.preferences)
        authViewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authViewModelFactory)[AuthViewModel::class]

        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this, birthdayFactory)[UsersBirthdayViewModel::class]

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.menuButtonToolbar

        UserFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            authViewModel,
            birthdayRepository,
            userSharedPreferences,
            "Settings"
        )

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.settingsToMainPage)
                    true
                }
                R.id.bottomNavTrashBin -> {
                    findNavController().navigate(R.id.settingsToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.settingsToPastBirthdays)
                    true
                }
                else -> false
            }
        }



        // Bildirim izinlerini kontrol et ve Switch'i ayarla
        updateNotificationSwitch()


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
            userSharedPreferences.setDarkThemeToggle(isChecked)
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

        binding.whatIsBirthifyCardView.setOnClickListener {
            val dialogFragment = WhatIsBirthifyDialogFragment()
            dialogFragment.show(parentFragmentManager, "WhatIsBirthifyDialog")
        }

        binding.faqCardView.setOnClickListener {
            val dialogFragment = FrequentlyAskedQuestionsDialogFragment()
            dialogFragment.show(parentFragmentManager, "FrequentlyAskedQuestionsDialog")
        }

        binding.contactUsCardView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("support@birthify.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(),"There isn't any e-mail app on your device", Toast.LENGTH_SHORT).show()
            }
        }

        binding.privacyPolicyCardView.setOnClickListener {
            val dialogFragment = PrivacyPolicyDialogFragment()
            dialogFragment.show(parentFragmentManager, "PrivacyPolicyDialog")
        }


        binding.logoutCardView.setOnClickListener {
            authViewModel.logoutUser()
            userSharedPreferences.clearUserSession()
            birthdayRepository.clearBirthdays()
            birthdayRepository.clearDeletedBirthdays()
            birthdayRepository.clearPastBirthdays()
            logout(requireActivity())
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


    private fun updateNotificationSwitch() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isNotificationEnabled = notificationManager.areNotificationsEnabled()
        binding.notificationSwitch.isChecked = isNotificationEnabled
    }

    private fun enableDarkTheme() {
        // Karanlık temayı etkinleştir
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun disableDarkTheme() {
        // Karanlık temayı devre dışı bırak
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onResume() {
        super.onResume()

        // Switch durumlarını güncelle
        updateNotificationSwitch()


        val isDarkThemeEnabled = userSharedPreferences.isDarkThemeEnabled()
        binding.darkThemeSwitch.isChecked = isDarkThemeEnabled
    }

    // Logout fonksiyonu
    private fun logout(activity: Activity) {
        // Mevcut aktiviteyi kapatır ve yeni bir aktivite başlatır yani uygulama sıfırdan başlamış gibi olur
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }




}
