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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentRegisterBinding

import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory

class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var viewModel : AuthViewModel
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var registerPassTextInput: TextInputLayout
    private lateinit var registerPasswordEditText: TextInputEditText
    private lateinit var registerFullNameTextInput: TextInputLayout
    private lateinit var registerFullNameEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register, container, false)

        val repository = AuthRepository(requireContext())
        val factory= AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[AuthViewModel::class.java]

        // Snackbar için view
        val view = (context as Activity).findViewById<View>(android.R.id.content)


        registerPassTextInput = binding.registerPassTextInput
        registerPasswordEditText = binding.registerPasswordEditText

        emailTextInputLayout = binding.emailRegisterTextInputLayout
        emailEditText = binding.emailRegisterEditText

        registerFullNameTextInput= binding.registerFullNameTextInput
        registerFullNameEditText = binding.registerFullNameEditText


        binding.signInRegisterTv.setOnClickListener {it.findNavController().navigate(R.id.registerToLogin)}
        binding.fabRegister.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.forgotPassRegisterTv.setOnClickListener { it.findNavController().navigate(R.id.registerToForgot) }



        // Register fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (isValidEmail(email)) {
                    emailTextInputLayout.error = null
                    emailTextInputLayout.isErrorEnabled = false //error yazıdı gittiğinde yazıdan kalan boşluk bu kod ile gider
                } else {
                    emailTextInputLayout.error = "Invalid email address"

                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                emailTextInputLayout.error = ""
                emailTextInputLayout.isErrorEnabled = false
            }
        }
        // Register fragment sayfasında girilen e-mail formatını kontrol ediyoruz



        // Register fragment sayfasında girilen şifre formatını kontrol ediyoruz
        registerPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (isValidPassword(password)) {
                    registerPassTextInput.error = ""
                    registerPassTextInput.isErrorEnabled = false
                } else {
                    registerPassTextInput.error = "Password must be at least 6 characters, include an uppercase letter, a number, and a punctuation mark"
                    registerPassTextInput.isErrorEnabled = true
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        registerPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                registerPassTextInput.error = ""
                registerPassTextInput.isErrorEnabled = false
            }
        }
        // Register fragment sayfasında girilen şifre formatını kontrol ediyoruz



        // Register fragment sayfasında girilen Full Name formatını kontrol ediyoruz
        registerFullNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val fullName = s.toString()
                if (isValidFullName(fullName)) {
                    registerFullNameTextInput.error = ""
                    registerFullNameTextInput.isErrorEnabled = false
                } else {
                    registerFullNameTextInput.error = "Full Name must be at least two words, without punctuation or numbers. Every words must contain at least 2 letter"
                    registerFullNameTextInput.isErrorEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        registerFullNameEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                registerFullNameTextInput.error = ""
                registerFullNameTextInput.isErrorEnabled = false
            }
        }
        // Register fragment sayfasında girilen Full Name formatını kontrol ediyoruz




        binding.registerButton.setOnClickListener {
            val name = binding.registerFullNameEditText.text.toString()
            val email = binding.emailRegisterEditText.text.toString()
            val password = binding.registerPasswordEditText.text.toString()

            if( isValidPassword(password)
                && isValidEmail(email)
                && isValidFullName(name)
                ){

                //user kaydetme fonksiyonunu viewmodeldan çağırıyoruz
                viewModel.registerUser(name,email,password)

                //user kaydedilene kadar view'i invisible edip lottie animasyonunu başlatıyoruz
                binding.registerFragmentTopLayout.visibility = View.INVISIBLE
                binding.registerLottieAnimation.visibility = View.VISIBLE
                binding.registerLottieAnimation.playAnimation()

                Handler(Looper.getMainLooper()).postDelayed({
                    Log.e("tagım", "handler")
                    viewModel.registrationState.observe(viewLifecycleOwner) { isSuccess ->
                        Log.e("tagım", "observer")

                        if (isSuccess) {
                            Snackbar.make(view,"Registration successfully, please verify your email",Snackbar.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.registerToLogin)
                        } else {
                            Snackbar.make(view,"Registration failed",Snackbar.LENGTH_SHORT).show()
                        }
                    }


                    // Observe işleminden sonra observe'ı kaldırır
                    viewModel.registrationState.removeObservers(viewLifecycleOwner)

                    //animasyonu durdurup view'i visible yapıyoruz
                    binding.registerLottieAnimation.cancelAnimation()
                    binding.registerLottieAnimation.visibility = View.INVISIBLE
                    binding.registerFragmentTopLayout.visibility = View.VISIBLE
                }, 3000)
            }else{
                Snackbar.make(view,"Please correctly fill in all fields",Snackbar.LENGTH_SHORT).show()
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
    private fun isValidPassword(password: String): Boolean {
        if (password.isBlank()) {
            return false
        }
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*\\W)(?=.{6,})\\S*$")
        return passwordPattern.matches(password)
    }
    private fun isValidFullName(fullName: String): Boolean {
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
        if (words.any { !namePattern.matches(it) }) {
            return false
        }

        return true
    }



}