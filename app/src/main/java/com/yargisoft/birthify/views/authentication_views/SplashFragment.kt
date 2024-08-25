package com.yargisoft.birthify.views.authentication_views

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var authRepository: AuthRepository
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        userSharedPreferencesManager = UserSharedPreferencesManager(requireContext())
        authRepository = AuthRepository(userSharedPreferencesManager.preferences,requireContext())


        // İzin isteme işlemini başlatmak için launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("NotificationPermission", "Bildirim izni verildi.")
            } else {
                Log.d("NotificationPermission", "Bildirim izni reddedildi.")
            }
            // İzin işlemi tamamlandıktan sonra oturum kontrolünü yap
            checkSession()
        }

        // SharedPreferences ile ilk açılışı kontrolü
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            // İlk açılışsa bildirim izni istenir
            requestNotificationPermission()
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
        } else {
            // İlk açılış değilse direkt oturum kontrolünü yap
            checkSession()
        }

        return view
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

        } else {
            checkSession() // Eski Android sürümlerinde direkt oturum kontrolüne geç
        }
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


