package com.yargisoft.birthify.views.auth_views

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentLoginPageBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale


class LoginPageFragment : Fragment() {

    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var userSharedPreferences : UserSharedPreferencesManager
//    private val args: LoginPageFragmentArgs by navArgs()  // navArgs kullanımı
    private lateinit var loginEmailTextInput: TextInputLayout
    private lateinit var loginEmailEditText: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_page, container, false)

        //userPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        //Validation için mail input kutularını tanımlıyoruz
        loginEmailTextInput = binding.loginEmailTextInput
        loginEmailEditText = binding.loginEmailEditText

//        if (args.sourcePage == "Register") {
//            loginEmailEditText.setText(args.email)
//            binding.loginPassEditText.setText(args.password)
//        }
       // Log.e("tagım", args.sourcePage)


        // Snackbar için view
        val view = (context as Activity).findViewById<View>(android.R.id.content)

        //repo factory ve viewmodel tanımlamaları
        val repository = AuthRepository(requireContext())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[AuthViewModel::class.java]


        // Login fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        loginEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (isValidEmail(email)) {
                    loginEmailTextInput.error = null
                    loginEmailTextInput.isErrorEnabled = false //error yazıdı gittiğinde yazıdan kalan boşluk bu kod ile gider
                } else {
                    loginEmailTextInput.error = "Invalid email address"

                }
            }

            override fun afterTextChanged(s: Editable?) {
                val cursorPosition = binding.loginEmailEditText.selectionStart
                val lowerCaseText = s.toString().lowercase(Locale.getDefault())
                if (s.toString() != lowerCaseText) {
                    binding.loginEmailEditText.setText(lowerCaseText)
                    binding.loginEmailEditText.setSelection(cursorPosition)
                }
            }
        })
        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        loginEmailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                loginEmailTextInput.error = ""
                loginEmailTextInput.isErrorEnabled = false
            }
        }
        // Login fragment sayfasında girilen e-mail formatını kontrol ediyoruz



        binding.forgotPassLoginTv.setOnClickListener { it.findNavController().navigate(R.id.loginToForgot) }
        binding.signUpLoginTv.setOnClickListener { it.findNavController().navigate(R.id.loginToRegister) }

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.loginPassEditText.text.toString()
            val isChecked = binding.rememberCBox.isChecked

            if (isValidEmail(email) && password.isNotEmpty()) {
                viewModel.loginUser(email, password,isChecked)
                binding.loginPageTopLayout.visibility = View.INVISIBLE
                binding.loginPageLottieAnimation.visibility = View.VISIBLE
                binding.loginPageLottieAnimation.playAnimation()

                Handler(Looper.getMainLooper()).postDelayed({
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.isLoading.collect { isLoading ->
                                Log.e("tagımıs", " login yüklenme durumu fragment: $isLoading")

                                if(!isLoading){
                                    //animasyonu durdurup view'i visible yapıyoruz
                                    binding.loginPageLottieAnimation.cancelAnimation()
                                    binding.loginPageLottieAnimation.visibility = View.INVISIBLE
                                    binding.loginPageTopLayout.visibility = View.VISIBLE

                                }
                            }
                        }
                    }
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.loginResult.collect { result ->
                                if (result == true) {
                                    if (viewModel.isEmailVerifiedResult != true){
                                        userSharedPreferences.clearUserSession()
                                        Snackbar.make(view,"Please verify your e-mail",Snackbar.LENGTH_SHORT).show()
                                    }
                                    else{
                                        if(isChecked){
                                            userSharedPreferences.saveIsChecked(true )
                                        }else{
                                            userSharedPreferences.saveIsChecked(false )
                                        }

                                        Snackbar.make(view,"You successfully logged in",Snackbar.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.loginToMain)
                                    }
                                }else{
                                    Snackbar.make(view,"Login failed",Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }, 5000) // 2 saniye bekletme


            }else {
                Snackbar.make(view,"Please fill in all fields",Snackbar.LENGTH_SHORT).show()
            }


        }

        return binding.root
    }


    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }


}