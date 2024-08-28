package com.yargisoft.birthify.views.authentication_views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.R
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.google.firebase.firestore.FirebaseFirestore
import com.yargisoft.birthify.repositories.AuthRepository

class SplashFragment : Fragment() {
    private lateinit var userSharedPreferencesManager: UserSharedPreferencesManager
    private lateinit var authRepository: AuthRepository
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        userSharedPreferencesManager = UserSharedPreferencesManager(requireContext())
        authRepository = AuthRepository(userSharedPreferencesManager.preferences,requireContext())

        checkSession()


        return view
    }



    private fun checkSession() {
        Handler(Looper.getMainLooper()).postDelayed({

            if (userSharedPreferencesManager.getToken() == "guest"){
                navigateToFragmentAndClearStack(findNavController(), R.id.splashFragment, R.id.splashToGuestNavGraph)

            }else if (authRepository.isUserLoggedIn()){

                val userId = userSharedPreferencesManager.getUserId()
                val storedToken = userSharedPreferencesManager.getToken() ?: ""

                verifyUserToken(userId, storedToken) { isValid ->
                    if (isValid) {
                        navigateToFragmentAndClearStack(findNavController(), R.id.splashFragment, R.id.splashToMainNavGraph)
                    } else {
                        Snackbar.make(requireView(),getString(R.string.logged_another_device),Snackbar.LENGTH_SHORT).show()
                        userSharedPreferencesManager.clearUserSession()
                        navigateToFragmentAndClearStack(findNavController(), R.id.splashFragment, R.id.splashToFirstPage)
                    }
                }
            }else{
                navigateToFragmentAndClearStack(findNavController(), R.id.splashFragment, R.id.splashToFirstPage)
            }
        }, 2500) // 2.5 saniye bekletme
    }

    private fun verifyUserToken(userId: String, storedToken: String, callback: (Boolean) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val firebaseToken = document.getString("token") ?: ""
                callback(storedToken == firebaseToken)
            }
            .addOnFailureListener { exception ->
                Log.e("TokenVerification", "Error verifying token: ${exception.message}")
                callback(false)
            }
    }

    private fun navigateToFragmentAndClearStack(navController: NavController, currentFragmentId: Int, targetFragmentId: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(currentFragmentId, inclusive = true)
            .build()

        navController.navigate(targetFragmentId, null, navOptions)
    }

}


