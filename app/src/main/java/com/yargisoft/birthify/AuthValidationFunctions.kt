package com.yargisoft.birthify

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.FrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.FrequentlyUsedFunctions.enableViewDisableLottie
import com.yargisoft.birthify.FrequentlyUsedFunctions.navigateToFragmentAndClearStack
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

object AuthValidationFunctions {

    fun registerValidation(
        email: String,
        password: String,
        name: String,
        viewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner: LifecycleOwner,
        view: View,
        findNavController: NavController,
        action: Int,
        navOptions: NavOptions,
        context: Context
    ) {
        if (isValidPassword(password) && isValidEmail(email) && isValidFullName(name)) {


            viewModel.registerUser(name, email, password)

            disableViewEnableLottie(lottieAnimationView, view)

            Handler(Looper.getMainLooper()).postDelayed({
                viewLifecycleOwner.lifecycleScope.launch {
                    var isLoadedEmitted = false // Kontrol değişkeni

                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.isLoaded.collect { isLoaded ->
                            if (isLoaded && !isLoadedEmitted) {
                                isLoadedEmitted = true // Tekrar çalışmasını engellemek için işaretler

                                val isSuccess = viewModel.authSuccess.value
                                val errorMessage = viewModel.authError.value

                                if (isSuccess) {
                                    Snackbar.make(view, getString(context,R.string.registration_successful), Snackbar.LENGTH_SHORT).show()
                                    findNavController.navigate(action, null, navOptions)
                                } else {
                                    Snackbar.make(view, errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                }

                                enableViewDisableLottie(lottieAnimationView, view)
                            }
                        }
                    }
                }
            },1500)


        } else {
            Snackbar.make(view, getString(context,R.string.fill_in_fields_correctly), Snackbar.LENGTH_SHORT).show()
        }
    }


    /*Şifre sıfırlama ekranında (Forgot Password Page) kutucuğa uazılan mailin validayion işlemlerini,
     animasyon işlemlerini vs yapan fonksiyon
     */
    fun resetPasswordValidation(
        email: String,
        viewLifecycleOwner: LifecycleOwner,
        viewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        view: View,
        findNavController: NavController,
        action: Int,
        navOptions: NavOptions,
        context: Context
    ) {
        if (isValidEmail(email)) {

            viewModel.resetPassword(email)

            disableViewEnableLottie(lottieAnimationView, view)

            Handler(Looper.getMainLooper()).postDelayed({
                viewLifecycleOwner.lifecycleScope.launch {
                    var isLoadedEmitted = false // Kontrol değişkeni

                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.isLoaded.collect { isLoaded ->
                            if (isLoaded && !isLoadedEmitted) {
                                isLoadedEmitted = true // Tekrar çalışmasını engellemek için işaretler

                                val isSuccess = viewModel.authSuccess.value
                                val errorMessage = viewModel.authError.value

                                if (isSuccess) {
                                    Snackbar.make(view, getString(context,R.string.password_reset_mail_successful), Snackbar.LENGTH_SHORT).show()
                                    findNavController.navigate(action, null, navOptions)
                                } else {
                                    Snackbar.make(view, errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                }

                                enableViewDisableLottie(lottieAnimationView, view)
                            }
                        }
                    }
                }

            },1500)

        } else {
            Snackbar.make(view, getString(context,R.string.please_enter_a_valid_email), Snackbar.LENGTH_SHORT).show()
        }
    }


    /*Login page içerisinde login butonuna tıkladığımızda, verilen email validasyonunu yapan,
       animasyonları gösterip devre dışı bırakan, kullanıcı etkileşimini sağlayan fonksiyon (snackbar iler)*/
    fun loginValidation(
        view: View,
        email: String,
        password: String,
        authViewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner: LifecycleOwner,
        findNavController: NavController,
        context: Context
    ) {
        if (isValidEmail(email) && password.isNotEmpty()) {

            authViewModel.loginUser(email, password)

            disableViewEnableLottie(lottieAnimationView, view)

            Handler(Looper.getMainLooper()).postDelayed({
                viewLifecycleOwner.lifecycleScope.launch {
                    var isLoadedEmitted = false // Kontrol değişkeni

                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        authViewModel.isLoaded.collect { isLoaded ->
                            if (isLoaded && !isLoadedEmitted) {
                                isLoadedEmitted = true // Tekrar çalışmasını engellemek için işaretler

                                val isSuccess = authViewModel.authSuccess.value
                                val errorMessage = authViewModel.authError.value

                                if (isSuccess) {
                                    Snackbar.make(view, getString(context,R.string.successfully_logged_in), Snackbar.LENGTH_SHORT).show()
                                    navigateToFragmentAndClearStack(findNavController, R.id.loginPageFragment, R.id.loginToMain)
                                } else {
                                    Snackbar.make(view, errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                }

                                // View'i tekrar aktif et ve animasyonu durdur
                                enableViewDisableLottie(lottieAnimationView, view)
                            }
                        }
                    }
                }

            },1500)

        } else {
            Snackbar.make(view, getString(context,R.string.fill_in_all_fields), Snackbar.LENGTH_SHORT).show()
        }
    }



    //Email, password ve fullname validation için kullanılan fonksiyonlar
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }
    fun isValidPassword(password: String): Boolean {
        if (password.isBlank()) {
            return false
        }
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*\\W)(?=.{6,12})\\S*$")
        return passwordPattern.matches(password)
    }
    fun isValidFullName(fullName: String): Boolean {
        // Boş olup olmadığını kontrol et
        if (fullName.isBlank()) {
            return false
        }

        // Kelimeleri ayır
        val words = fullName.trim().split("\\s+".toRegex())

        // En az iki kelime ve her kelimenin en az 3 harf uzunluğunda olması gerekiyor
        if (words.size < 2 || words.any { it.length < 2 }) {
            return false
        }

        // Kelimelerde rakam veya noktalama işareti olmamalı
        val namePattern = Regex("^[a-zA-Z]+$")

        return !words.any { !namePattern.matches(it) }
    }
    //Email, password ve fullname validation için kullanılan fonksiyonlar


}