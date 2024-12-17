package com.yargisoft.birthify.ui.views.auth

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.utils.helpers.AuthValidationFunctions.isValidEmail
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.enableViewDisableLottie
import com.yargisoft.birthify.ui.activities.MainActivity
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentLoginPageBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class LoginPageFragment : Fragment() {

    private val  viewModel: AuthViewModel by viewModels()

    private var _binding : FragmentLoginPageBinding? = null
    private val binding get() = _binding!!


     private lateinit var loginEmailTextInput: TextInputLayout
     private lateinit var loginEmailEditText: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        _binding = FragmentLoginPageBinding.inflate(inflater , container, false)



        //Validation için mail input kutularını tanımlıyoruz
        loginEmailTextInput = binding.loginEmailTextInput
        loginEmailEditText = binding.loginEmailEditText




        // Login fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        loginEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (isValidEmail(email)) {
                    loginEmailTextInput.error = null
                    loginEmailTextInput.isErrorEnabled = false //error yazıdı gittiğinde yazıdan kalan boşluk bu kod ile gider
                } else {
                    loginEmailTextInput.error = getString(R.string.invalid_email_adress)

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


        binding.forgotPassLoginTv.setOnClickListener {
            findNavController().navigate(R.id.loginToForgot)
        }
        binding.dontHaveTv.setOnClickListener {
            findNavController().navigate(R.id.loginToRegister)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.loginPassEditText.text.toString()
            loginValidation(
                email,
                password,
                viewModel,
                binding.threePointAnimation,
                viewLifecycleOwner,
                )
        }

        return binding.root
    }

    /*Login page içerisinde login butonuna tıkladığımızda, verilen email validasyonunu yapan,
       animasyonları gösterip devre dışı bırakan, kullanıcı etkileşimini sağlayan fonksiyon (snackbar iler)*/
    private fun loginValidation(
        email: String,
        password: String,
        authViewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner: LifecycleOwner,
    ) {
        if (isValidEmail(email) && password.isNotEmpty()) {

            authViewModel.loginUser(email, password)

            disableViewEnableLottie(lottieAnimationView, requireView())

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
                                    Snackbar.make(requireView(),
                                        ContextCompat.getString(
                                            requireContext(),
                                            R.string.successfully_logged_in
                                        ), Snackbar.LENGTH_SHORT).show()
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                } else {
                                    Snackbar.make(requireView(), errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                }

                                // View'i tekrar aktif et ve animasyonu durdur
                                enableViewDisableLottie(lottieAnimationView, requireView())
                            }
                        }
                    }
                }

            },1500)

        } else {
            Snackbar.make(requireView(),
                ContextCompat.getString(requireContext(), R.string.fill_in_all_fields), Snackbar.LENGTH_SHORT).show()
        }
    }


}