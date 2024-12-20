package com.yargisoft.birthify.ui.views.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentRegisterBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.utils.helpers.AuthValidationFunctions.isValidFullName
import com.yargisoft.birthify.utils.helpers.AuthValidationFunctions.isValidPassword
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.enableViewDisableLottie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel : AuthViewModel by viewModels()

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

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)


        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.registerFragment, inclusive = true)
            .build()


        registerPassTextInput = binding.passwordTextInput
        registerPasswordEditText = binding.passwordEditText

        emailTextInputLayout = binding.emailTextInput
        emailEditText = binding.emailEditText

        registerFullNameTextInput= binding.fullNameTextInput
        registerFullNameEditText = binding.fullNameEditText


        binding.alreadyHaveAccountTv.setOnClickListener {
            FrequentlyUsedFunctions
                .navigateToFragmentAndClearStack(findNavController(),R.id.registerFragment,R.id.registerToLogin)
        }
        binding.forgotPasswordTv.setOnClickListener {
            FrequentlyUsedFunctions
                .navigateToFragmentAndClearStack(findNavController(),R.id.registerFragment,R.id.registerToForgot)
        }



        // Register fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (viewModel.isEmailValid(email)) {
                    emailTextInputLayout.error = null
                    emailTextInputLayout.isErrorEnabled = false //error yazıdı gittiğinde yazıdan kalan boşluk bu kod ile gider
                } else {
                    emailTextInputLayout.error = getString(R.string.invalid_email_adress)

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
                if (isValidPassword(password)) {
                    registerPassTextInput.error = ""
                    registerPassTextInput.isErrorEnabled = false
                } else {
                    registerPassTextInput.error = getString(R.string.password_error)
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
                    registerFullNameTextInput.error = getString(R.string.name_error)
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

           registerValidation(
                email,
                password,
                name,
                viewModel,
                binding.registerAnimation,
                viewLifecycleOwner,
                R.id.registerToLogin,
                navOptions)
        }
        return binding.root
    }


    private fun registerValidation(
        email: String,
        password: String,
        name: String,
        viewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner: LifecycleOwner,
        action: Int,
        navOptions: NavOptions,

    ) {
        if (isValidPassword(password) && viewModel.isEmailValid(email) && isValidFullName(name)) {

            viewModel.registerUser(name, email, password)

            disableViewEnableLottie(lottieAnimationView, requireView())

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
                                    Snackbar.make(requireView(),
                                        ContextCompat.getString(
                                            requireContext(),
                                            R.string.registration_successful
                                        ), Snackbar.LENGTH_SHORT).show()
                                    findNavController().navigate(action, null, navOptions)
                                } else {
                                    Snackbar.make(requireView(), errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                }

                                enableViewDisableLottie(lottieAnimationView, requireView())
                            }
                        }
                    }
                }
            },1500)


        } else {
            Snackbar.make(requireView(),
                ContextCompat.getString(requireContext(), R.string.fill_in_fields_correctly), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}