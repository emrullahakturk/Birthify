package com.yargisoft.birthify.views.guest_authentication_views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.databinding.FragmentGuestLoginPageBinding
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class GuestLoginPageFragment : Fragment() {

    private lateinit var  binding : FragmentGuestLoginPageBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val guestViewModel: GuestBirthdayViewModel by viewModels()
    private lateinit var loginEmailTextInput: TextInputLayout
    private lateinit var loginEmailEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_guest_login_page, container, false)

        loginEmailEditText = binding.loginEmailEditText
        loginEmailTextInput = binding.loginEmailTextInput


        guestViewModel.getBirthdays()
        guestViewModel.getPastBirthdays()
        guestViewModel.getDeletedBirthdays()


        // Login fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        loginEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (UserFrequentlyUsedFunctions.isValidEmail(email)) {
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
                findNavController().navigate(R.id.guestLoginToReset)
        }
        binding.dontHaveTv.setOnClickListener {
                findNavController().navigate(R.id.guestLoginToRegister)
        }


        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.loginPassEditText.text.toString()
            GuestFrequentlyUsedFunctions.loginGuestValidation(
                binding.root,
                email,
                password,
                authViewModel,
                binding.threePointAnimation,
                viewLifecycleOwner,
                findNavController(),
                requireContext(),
                guestViewModel,
                requireActivity()
            )
        }
        return binding.root
    }


}