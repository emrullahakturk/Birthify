package com.yargisoft.birthify.ui.views.authfragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.AuthValidationFunctions
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentForgotPasswordBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()



    private lateinit var forgotPassTextInputLayout : TextInputLayout
    private lateinit var resetPassEmailEditText: TextInputEditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding=  DataBindingUtil.inflate(inflater,R.layout.fragment_forgot_password, container, false)

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
                if (AuthValidationFunctions.isValidEmail(email)) {
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

           AuthValidationFunctions.resetPasswordValidation(
               email,
               viewLifecycleOwner,
               viewModel,
               binding.forgotPasswordLottie,
               binding.root,
               findNavController(),
               R.id.forgotToLogin,
               navOptions,
               requireContext()
           )

        }
        binding.rememberedPassTv.setOnClickListener {
            FrequentlyUsedFunctions
                .navigateToFragmentAndClearStack(findNavController(),R.id.forgotPasswordFragment,R.id.forgotToLogin)
        }
        

        return binding.root

    }



}