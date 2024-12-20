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
import com.yargisoft.birthify.databinding.FragmentForgotPasswordBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.enableViewDisableLottie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var forgotPassTextInputLayout : TextInputLayout
    private lateinit var resetPassEmailEditText: TextInputEditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding=  FragmentForgotPasswordBinding.inflate(inflater, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.registerFragment, inclusive = true)
            .build()



        //Text Change Listener için edittext ve textinputlayout tanımlamaları
        resetPassEmailEditText = binding.resetPassEmailEditText
        forgotPassTextInputLayout = binding.forgotPassTextInputLayout


        // Forgot Password fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        resetPassEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (viewModel.isEmailValid(email)) {
                    forgotPassTextInputLayout.error = null
                    forgotPassTextInputLayout.isErrorEnabled = false //error yazıdı gittiğinde yazıdan kalan boşluk bu kod ile gider
                } else {
                    forgotPassTextInputLayout.error = "Invalid email address"

                }
            }

            override fun afterTextChanged(s: Editable?) {
                val cursorPosition = binding.resetPassEmailEditText.selectionStart
                val lowerCaseText = s.toString().lowercase(Locale.getDefault())
                if (s.toString() != lowerCaseText) {
                    binding.resetPassEmailEditText.setText(lowerCaseText)
                    binding.resetPassEmailEditText.setSelection(cursorPosition)
                }
            }
        })
        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        resetPassEmailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                forgotPassTextInputLayout.error = ""
                forgotPassTextInputLayout.isErrorEnabled = false
            }
        }
        // Forgot Password fragment sayfasında girilen e-mail formatını kontrol ediyoruz


        binding.resetPassButton.setOnClickListener {

            val email = binding.resetPassEmailEditText.text.toString()

          resetPasswordValidation(
               email,
               viewLifecycleOwner,
               viewModel,
               binding.forgotPasswordLottie,
               R.id.forgotToLogin,
               navOptions,

           )

        }
        binding.rememberedPassTv.setOnClickListener {
            FrequentlyUsedFunctions
                .navigateToFragmentAndClearStack(findNavController(),R.id.forgotPasswordFragment,R.id.forgotToLogin)
        }
        

        return binding.root

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /*Şifre sıfırlama ekranında (Forgot Password Page) kutucuğa uazılan mailin validayion işlemlerini,
   animasyon işlemlerini vs yapan fonksiyon
   */
   private fun resetPasswordValidation(
        email: String,
        viewLifecycleOwner: LifecycleOwner,
        viewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        action: Int,
        navOptions: NavOptions,
    ) {
        if (viewModel.isEmailValid(email)) {

            viewModel.resetPassword(email)

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
                                            R.string.password_reset_mail_successful
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
                ContextCompat.getString(requireContext(), R.string.please_enter_a_valid_email), Snackbar.LENGTH_SHORT).show()
        }
    }



}