package com.yargisoft.birthify.views.auth_views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.sharedpreferences.BirthdaySharedPreferencesManager
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager


class SplashFragment : Fragment() {
    private lateinit var userSharedPreferencesManager: UserSharedPreferencesManager
    private lateinit var birthdaySharedPreferencesManager: BirthdaySharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        userSharedPreferencesManager = UserSharedPreferencesManager(requireContext())
        birthdaySharedPreferencesManager = BirthdaySharedPreferencesManager(requireContext())

        checkSession()

        return view
    }

    private fun checkSession() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (userSharedPreferencesManager.checkIsUserLoggedIn()) {
                // Oturum bilgileri mevcutsa MainFragment'a yönlendirir
                findNavController().navigate(R.id.splashToMainPage)
            } else {
                userSharedPreferencesManager.clearUserSession()
                birthdaySharedPreferencesManager.clearBirthdays()
                findNavController().navigate(R.id.splashToFirstPage)
            }
        }, 2500) // 2 saniye bekletme
    }
}
