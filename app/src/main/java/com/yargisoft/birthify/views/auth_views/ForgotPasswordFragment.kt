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
import com.yargisoft.birthify.databinding.FragmentForgotPasswordBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import java.util.Locale


class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var forgotPassTextInputLayout : TextInputLayout
    private lateinit var resetPassEmailEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding=  DataBindingUtil.inflate(inflater,R.layout.fragment_forgot_password, container, false)
        binding.forgotPassSignInTv.setOnClickListener { it.findNavController().navigate(R.id.forgotToLogin) }



        //viewModel tanımlama için gerekli kodlar
        val repository = AuthRepository(requireContext())
        val factory= AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory )[AuthViewModel::class.java]

        // Snackbar için view
        //val view = (context as Activity).findViewById<View>(android.R.id.content)

        //Text Change Listener için edittext ve textinputlayout tanımlamaları
        resetPassEmailEditText = binding.resetPassEmailEditText
        forgotPassTextInputLayout = binding.forgotPassTextInputLayout


        // Forgot Password fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        resetPassEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (FrequentlyUsedFunctions.isValidEmail(email)) {
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

            binding.forgotPassTopLayout.visibility = View.INVISIBLE
            binding.forgotPasswordLottie.visibility = View.VISIBLE
            binding.forgotPasswordLottie.playAnimation()

            viewModel.resetPassword(email)

           FrequentlyUsedFunctions.resetPasswordValidation(
               viewLifecycleOwner,
               viewModel,
               binding.forgotPasswordLottie,
               binding.root,
               findNavController(),
               binding.forgotPassTopLayout
           )

        }

        return binding.root

    }



}