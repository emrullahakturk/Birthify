package com.yargisoft.birthify.views.authentication_views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentRegisterBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import java.util.Locale

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

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.registerFragment, inclusive = true)
            .build()

        val repository = AuthRepository(requireContext())
        val factory= AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[AuthViewModel::class.java]

        // Snackbar için view
        //val view = (context as Activity).findViewById<View>(android.R.id.content)


        registerPassTextInput = binding.passwordTextInput
        registerPasswordEditText = binding.passwordEditText

        emailTextInputLayout = binding.emailTextInput
        emailEditText = binding.emailEditText

        registerFullNameTextInput= binding.fullNameTextInput
        registerFullNameEditText = binding.fullNameEditText


        binding.alreadyHaveAccountTv.setOnClickListener {
            UserFrequentlyUsedFunctions
                .navigateToFragmentAndClearStack(findNavController(),R.id.registerFragment,R.id.registerToLogin)
        }
        binding.forgotPasswordTv.setOnClickListener {
            UserFrequentlyUsedFunctions
                .navigateToFragmentAndClearStack(findNavController(),R.id.registerFragment,R.id.registerToForgot)
        }



        // Register fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (UserFrequentlyUsedFunctions.isValidEmail(email)) {
                    emailTextInputLayout.error = null
                    emailTextInputLayout.isErrorEnabled = false //error yazıdı gittiğinde yazıdan kalan boşluk bu kod ile gider
                } else {
                    emailTextInputLayout.error = "Invalid email address"

                }
            }

            override fun afterTextChanged(s: Editable?) {
                val cursorPosition = binding.emailEditText.selectionStart
                val lowerCaseText = s.toString().lowercase(Locale.getDefault())
                if (s.toString() != lowerCaseText) {
                    binding.emailEditText.setText(lowerCaseText)
                    binding.emailEditText.setSelection(cursorPosition)
                }
            }
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
                if (UserFrequentlyUsedFunctions.isValidPassword(password)) {
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
                if (UserFrequentlyUsedFunctions.isValidFullName(fullName)) {
                    registerFullNameTextInput.error = ""
                    registerFullNameTextInput.isErrorEnabled = false
                } else {
                    registerFullNameTextInput.error = "Full Name must be at least two words, without punctuation or numbers. Every words must contain at least 2 letter"
                    registerFullNameTextInput.isErrorEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val formattedText = it.toString().split(" ").joinToString(" ") { word ->
                        word.lowercase().replaceFirstChar { char -> char.uppercase() }
                    }
                    if (formattedText != it.toString()) {
                        binding.fullNameEditText.setText(formattedText)
                        binding.fullNameEditText.setSelection(formattedText.length)
                    }
                }
            }
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

            val name = binding.fullNameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            UserFrequentlyUsedFunctions.registerValidationFunction(
                email,
                password,
                name,
                viewModel,
                binding.registerAnimation,
                viewLifecycleOwner,
                binding.root,
                findNavController(),
                R.id.registerToLogin,
                navOptions
                )
        }
        return binding.root
    }
}