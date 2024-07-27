package com.yargisoft.birthify.views.auth_views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentLoginPageBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
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
        //val view = (context as Activity).findViewById<View>(android.R.id.content)

        //repo factory ve viewmodel tanımlamaları
        val repository = AuthRepository(requireContext())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[AuthViewModel::class.java]


        // Login fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        loginEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (FrequentlyUsedFunctions.isValidEmail(email)) {
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
            FrequentlyUsedFunctions.loginValidationFunction(
                requireView(),
                email,
                password,
                isChecked,
                viewModel,
                binding.loginPageLottieAnimation,
                viewLifecycleOwner,
                userSharedPreferences,
                findNavController()
                )
        }
        return binding.root
    }
}