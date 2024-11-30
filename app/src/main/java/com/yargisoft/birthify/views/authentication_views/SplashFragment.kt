package com.yargisoft.birthify.views.authentication_views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.yargisoft.birthify.R
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var firestore :FirebaseFirestore
    @Inject lateinit var userSharedPreferencesManager: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
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


