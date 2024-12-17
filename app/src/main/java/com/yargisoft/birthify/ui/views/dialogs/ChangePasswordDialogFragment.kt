package com.yargisoft.birthify.ui.views.dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.utils.helpers.AuthValidationFunctions.isValidPassword
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.enableViewDisableLottie
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentChangePasswordDialogBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordDialogFragment : DialogFragment() {

    private val authViewModel: AuthViewModel by viewModels()
    private var _binding: FragmentChangePasswordDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        _binding = FragmentChangePasswordDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        val currentPasswordEditText = binding.currentPasswordEditText
        val newPasswordEditText = binding.newPasswordEditText
        val newPasswordTextInput = binding.newPasswordTextInput
        val confirmPasswordEditText = binding.confirmPasswordEditText
        val confirmPasswordTextInput = binding.confirmPasswordTextInput

        val changePasswordButton = binding.changePasswordButton
        val closeButton = binding.closeButton
        val lottieAnimationView = binding.threePointAnimation

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            disableViewEnableLottie(lottieAnimationView, binding.root)

            if (newPassword == confirmPassword) {
                if (isValidPassword(newPassword)) {
                    authViewModel.updatePassword(currentPassword, newPassword)
                    Handler(Looper.getMainLooper()).postDelayed({
                        lifecycleScope.launch {
                            var isLoadedEmitted = false // Kontrol değişkeni

                            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                authViewModel.isLoaded.collect { isLoaded ->
                                    if (isLoaded && !isLoadedEmitted) {
                                        isLoadedEmitted = true // Tekrar çalışmasını engellemek için işaretler

                                        val isSuccess = authViewModel.authSuccess.value
                                        val errorMessage = authViewModel.authError.value

                                        if (isSuccess) {
                                            Snackbar.make(
                                                binding.root,
                                                getString(R.string.changed_password_snackbar),
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                            delay(2000)
                                            dialog.dismiss()
                                        } else {
                                            Snackbar.make(
                                                binding.root,
                                                errorMessage ?: "Unknown error",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                        enableViewDisableLottie(lottieAnimationView, binding.root)
                                    }
                                }
                            }
                        }
                    }, 1500)

                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.enter_password_snackbar),
                        2000
                    ).show()
                    enableViewDisableLottie(lottieAnimationView, binding.root)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.password_does_not_match_snackbar),
                    2000
                ).show()
                enableViewDisableLottie(lottieAnimationView, binding.root)
            }
        }

        // Şifre format kontrolü
        newPasswordEditText.addTextChangedListener(createPasswordTextWatcher(newPasswordTextInput))
        confirmPasswordEditText.addTextChangedListener(createPasswordTextWatcher(confirmPasswordTextInput))

        return dialog
    }

    private fun createPasswordTextWatcher(textInputLayout: com.google.android.material.textfield.TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (isValidPassword(password)) {
                    textInputLayout.error = ""
                    textInputLayout.isErrorEnabled = false
                } else {
                    textInputLayout.error = getString(R.string.password_error)
                    textInputLayout.isErrorEnabled = true
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
