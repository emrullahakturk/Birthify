package com.yargisoft.birthify.views.AuthViews

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.sharedpreferences.SharedPreferencesManager


class SplashFragment : Fragment() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        checkSession()

        return view
    }

    private fun checkSession() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPreferencesManager.checkIsUserLoggedIn()) {
                // Oturum bilgileri mevcutsa MainFragment'a yönlendir
                findNavController().navigate(R.id.splashToMainPage)
            } else {
                // Oturum bilgileri yoksa LoginFragment'a yönlendir
                findNavController().navigate(R.id.splashToFirstPage)
            }
        }, 1000) // 2 saniye bekletme
    }
}
