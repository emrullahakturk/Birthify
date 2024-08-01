package com.yargisoft.birthify.views.authentication_views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager


class SplashFragment : Fragment() {
    private lateinit var userSharedPreferencesManager: UserSharedPreferencesManager
    private val navOptions = NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()

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
                    findNavController().navigate(R.id.splashToMainPage, null, navOptions)
                }else{
                    findNavController().navigate(R.id.splashToGuestNavGraph)
                }

            } else {
                userSharedPreferencesManager.clearUserSession()
                findNavController().navigate(R.id.splashToFirstPage, null, navOptions)
            }
        }, 3000) // 3 saniye bekletme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Geri tuşu işleyicisini ekleyin
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri gitmeyi engelle
                // Bu durumda, geri gitme işlevi engellenmiş olur
                // Buraya istediğiniz ek işlemleri de ekleyebilirsiniz (örneğin, bir uyarı göstermek)
            }
        })
    }



}
