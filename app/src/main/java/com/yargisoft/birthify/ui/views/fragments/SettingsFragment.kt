package com.yargisoft.birthify.ui.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.AuthActivity
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthSettingsBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.ui.views.dialogs.PrivacyPolicyDialogFragment
import com.yargisoft.birthify.ui.views.dialogs.WhatIsBirthifyDialogFragment
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.DARK_THEME_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.LANGUAGE_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_SETTINGS
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentAuthSettingsBinding
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    @Inject
    lateinit var birthdayRepository: BirthdayRepository
    private lateinit var preferences: SharedPreferences


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        handleNotificationPermissionResult(isGranted)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_settings, container, false)
        preferences = requireContext().getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)

        initializeUI()
        setupListeners()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUIState()
    }

    // Kullanıcı Arayüzü Bileşenlerini Başlatma
    private fun initializeUI() {
        updateNotificationSwitch()
        binding.darkThemeSwitch.isChecked = preferences.getBoolean(DARK_THEME_KEY, false)
    }

    // Kullanıcı Etkileşimlerini Tanımlama
    private fun setupListeners() {
        setupNotificationSwitch()
        setupDarkThemeSwitch()
        setupLanguageSelection()
        setupCardViewListeners()
        setupLogoutListener()
        setupBackButtonListener()
    }

    // Bildirim Switch'i Yönetimi
    private fun setupNotificationSwitch() {
        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) enableNotifications() else disableNotifications()
        }
    }

    // Karanlık Tema Switch'i Yönetimi
    private fun setupDarkThemeSwitch() {
        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) enableDarkTheme() else disableDarkTheme()
        }
    }

    // Dil Seçimi
    private fun setupLanguageSelection() {
        val currentLanguage = getCurrentLanguage()
        binding.languageSelectImageView.setOnClickListener {
            showLanguageSelectionDialog(currentLanguage)
        }
    }

    // Kart Görünümü Etkileşimlerini Tanımlama
    private fun setupCardViewListeners() {
        binding.whatIsBirthifyCardView.setOnClickListener {
            WhatIsBirthifyDialogFragment().show(parentFragmentManager, "WhatIsBirthifyDialog")
        }

        binding.faqCardView.setOnClickListener {
            com.yargisoft.birthify.ui.views.dialogs.FrequentlyAskedQuestionsDialogFragment()
                .show(parentFragmentManager, "FrequentlyAskedQuestionsDialog")
        }

        binding.contactUsCardView.setOnClickListener { sendSupportEmail() }

        binding.privacyPolicyCardView.setOnClickListener {
            PrivacyPolicyDialogFragment().show(parentFragmentManager, "PrivacyPolicyDialog")
        }
    }

    // Çıkış İşlemini Tanımlama
    private fun setupLogoutListener() {
        binding.logoutCardView.setOnClickListener {
            performLogout()
        }
    }

    // Geri Butonunu Tanımlama
    private fun setupBackButtonListener() {
        binding.fabBackButtonDetail.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Çıkış İşlemi
    private fun performLogout() {
        authViewModel.logoutUser()
        userSharedPreferences.clearUserSession()
        birthdayRepository.clearAllBirthdays()
        logout(requireActivity())
    }

    // Bildirim İzni Sonucunu Yönetme
    private fun handleNotificationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            Log.d("NotificationPermission", "Bildirim izni verildi.")
        } else {
            Log.d("NotificationPermission", "Bildirim izni reddedildi.")
        }
    }

    // Mevcut Dil Seçimini Alma
    private fun getCurrentLanguage(): String {
        val preferences = requireContext().getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        return preferences.getString(LANGUAGE_KEY, Locale.getDefault().language) ?: Locale.getDefault().language
    }

    // Dil Seçim Diyaloğunu Gösterme
    private fun showLanguageSelectionDialog(currentLanguage: String) {
        val languages = arrayOf("English", "Türkçe")
        val languageCodes = arrayOf("en", "tr")

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_language_title))
            .setItems(languages) { _, which ->
                val selectedLanguage = languageCodes[which]
                if (selectedLanguage != currentLanguage) setLocale(selectedLanguage)
            }
            .show()
    }

    // Uygulama Dilini Ayarlama
    private fun setLocale(languageCode: String) {
        val preferences = requireContext().getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        preferences.edit().putString(LANGUAGE_KEY, languageCode).apply()

        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    // Bildirimleri Etkinleştirme
    private fun enableNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                startActivity(intent)
            }
        }
    }

    // Bildirimleri Devre Dışı Bırakma
    private fun disableNotifications() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        startActivity(intent)
    }

    // Bildirim Durumunu Güncelleme
    private fun updateNotificationSwitch() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        binding.notificationSwitch.isChecked = notificationManager.areNotificationsEnabled()
    }

    // Karanlık Temayı Etkinleştirme
    private fun enableDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        preferences.edit().putBoolean(DARK_THEME_KEY, true).apply()
    }

    // Karanlık Temayı Devre Dışı Bırakma
    private fun disableDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        preferences.edit().putBoolean(DARK_THEME_KEY, false).apply()
    }

    // Oturum Kapatma
    private fun logout(activity: Activity) {
        userSharedPreferences.clearUserSession()
        val intent = Intent(activity, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

    // Destek E-postası Gönderme
    private fun sendSupportEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@birthify.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.support_email_snackbar), Toast.LENGTH_SHORT).show()
        }
    }

    // Kullanıcı Arayüzü Durumunu Güncelleme
    private fun updateUIState() {
        updateNotificationSwitch()
        binding.darkThemeSwitch.isChecked = preferences.getBoolean(DARK_THEME_KEY, false)
    }
}
