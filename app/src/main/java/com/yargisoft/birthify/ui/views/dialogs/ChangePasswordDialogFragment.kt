package com.yargisoft.birthify.ui.views.dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.AuthValidationFunctions.isValidPassword
import com.yargisoft.birthify.FrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.FrequentlyUsedFunctions.enableViewDisableLottie
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentChangePasswordDialogBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordDialogFragment : DialogFragment() {

    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: FragmentChangePasswordDialogBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_change_password_dialog, null, false)
        dialog.setContentView(binding.root)


        val currentPasswordEditText = binding.currentPasswordEditText
        val newPasswordEditText = binding.newPasswordEditText
        val newPasswordTextInput = binding.newPasswordTextInput
        val confirmPasswordEditText = binding.confirmPasswordEditText
        val confirmPasswordTextInput = binding.confirmPasswordTextInput



        val changePasswordButton = dialog.findViewById<Button>(R.id.changePasswordButton)
        val closeButton = dialog.findViewById<ImageButton>(R.id.closeButton)
        val lottieAnimationView = dialog.findViewById<LottieAnimationView>(R.id.three_point_animation)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            disableViewEnableLottie(lottieAnimationView, dialog.findViewById(android.R.id.content))

            if (newPassword == confirmPassword) {
                    if(isValidPassword(newPassword)){
                        authViewModel.updatePassword(currentPassword, newPassword)
                        Handler(Looper.getMainLooper()).postDelayed({
                            this@ChangePasswordDialogFragment.lifecycleScope.launch {
                                var isLoadedEmitted = false // Kontrol değişkeni

                                this@ChangePasswordDialogFragment.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                    authViewModel.isLoaded.collect { isLoaded ->
                                        if (isLoaded && !isLoadedEmitted) {
                                            isLoadedEmitted = true // Tekrar çalışmasını engellemek için işaretler

                                            val isSuccess = authViewModel.authSuccess.value
                                            val errorMessage = authViewModel.authError.value

                                            if (isSuccess) {
                                                Snackbar.make(dialog.findViewById(android.R.id.content), getString(R.string.changed_password_snackbar), Snackbar.LENGTH_SHORT).show()
                                                delay(2000)
                                                dialog.dismiss()
                                            } else {
                                                Snackbar.make(dialog.findViewById(android.R.id.content), errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                            }
                                            enableViewDisableLottie(lottieAnimationView, dialog.findViewById(android.R.id.content))
                                        }
                                    }
                                }
                            }

                        }, 1500)

                    }else{
                        Snackbar.make(dialog.findViewById(android.R.id.content), getString(R.string.enter_password_snackbar), 2000).show()
                        enableViewDisableLottie(lottieAnimationView, dialog.findViewById(android.R.id.content))
                    }
            }else {
                Snackbar.make(dialog.findViewById(android.R.id.content), getString(R.string.password_does_not_match_snackbar), 2000).show()
                enableViewDisableLottie(lottieAnimationView, dialog.findViewById(android.R.id.content))
            }
        }

        // Dialog fragment sayfasında girilen şifre formatını kontrol ediyoruz
        newPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (isValidPassword(password)) {
                    newPasswordTextInput.error = ""
                    newPasswordTextInput.isErrorEnabled = false
                } else {
                    newPasswordTextInput.error = getString(R.string.password_error)
                    newPasswordTextInput.isErrorEnabled = true
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        newPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                newPasswordTextInput.error = ""
                newPasswordTextInput.isErrorEnabled = false
            }
        }
        // Dialog fragment sayfasında girilen şifre formatını kontrol ediyoruz


        // Dialog fragment sayfasında girilen şifre formatını kontrol ediyoruz
        confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (isValidPassword(password)) {
                    confirmPasswordTextInput.error = ""
                    confirmPasswordTextInput.isErrorEnabled = false
                } else {
                    confirmPasswordTextInput.error =  getString(R.string.password_error)
                    confirmPasswordTextInput.isErrorEnabled = true
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        confirmPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                confirmPasswordTextInput.error = ""
                confirmPasswordTextInput.isErrorEnabled = false
            }
        }
        // Dialog fragment sayfasında girilen şifre formatını kontrol ediyoruz


        return dialog
    }
}
