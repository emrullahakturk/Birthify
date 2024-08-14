package com.yargisoft.birthify.views.guest_authentication_views

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
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.databinding.FragmentForgotPasswordBinding
import com.yargisoft.birthify.databinding.FragmentGuestResetPasswordBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import java.util.Locale


class GuestResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentGuestResetPasswordBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var forgotPassTextInputLayout : TextInputLayout
    private lateinit var resetPassEmailEditText: TextInputEditText
    private lateinit var userSharedPreferences: UserSharedPreferencesManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=  DataBindingUtil.inflate(inflater,R.layout.fragment_guest_reset_password, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.guestResetPasswordFragment, inclusive = true)
            .build()

        userSharedPreferences = UserSharedPreferencesManager(requireContext())


        //viewModel tanımlama için gerekli kodlar
        val repository = AuthRepository(userSharedPreferences.preferences)
        val factory= AuthViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory )[AuthViewModel::class.java]

        //Text Change Listener için edittext ve textinputlayout tanımlamaları
        resetPassEmailEditText = binding.resetPassEmailEditText
        forgotPassTextInputLayout = binding.forgotPassTextInputLayout




        // Forgot Password fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        resetPassEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (UserFrequentlyUsedFunctions.isValidEmail(email)) {
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

           GuestFrequentlyUsedFunctions.resetPasswordGuestValidation(
                email,
                viewLifecycleOwner,
                authViewModel,
                binding.forgotPasswordLottie,
                binding.root,
                findNavController(),
                R.id.guestResetToLogin,
                navOptions
            )

        }
        binding.rememberedPassTv.setOnClickListener {
            GuestFrequentlyUsedFunctions
                .navigateToFragmentAndClearStack(findNavController(),R.id.guestResetPasswordFragment,R.id.guestResetToLogin)
        }


        return binding.root
    }
}