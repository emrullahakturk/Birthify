package com.yargisoft.birthify.views.authentication_views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager


class SplashFragment : Fragment() {
    private lateinit var userSharedPreferencesManager: UserSharedPreferencesManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        userSharedPreferencesManager = UserSharedPreferencesManager(requireContext())
        checkSession()
        // Bir fragment'tan diğerine geçiş yaparken


        return view
    }


    private fun checkSession() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (userSharedPreferencesManager.checkIsUserLoggedIn()) {
                // Oturum bilgileri mevcutsa MainFragment'a yönlendirir
                if (userSharedPreferencesManager.getUserId() != "guest" ){
                    navigateToFragmentAndClearStack(findNavController(),R.id.splashFragment, R.id.splashToMainPage )

                }else{
                    navigateToFragmentAndClearStack(findNavController(),R.id.splashFragment, R.id.splashToGuestNavGraph )
                }

            } else {
                userSharedPreferencesManager.clearUserSession()
                navigateToFragmentAndClearStack(findNavController(),R.id.splashFragment, R.id.splashToFirstPage )
            }
        }, 3000) // 3 saniye bekletme
    }

    private fun navigateToFragmentAndClearStack(navController: NavController, currentFragmentId: Int, targetFragmentId: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(currentFragmentId, inclusive = true)
            .build()

        navController.navigate(targetFragmentId, null, navOptions)
    }


}
