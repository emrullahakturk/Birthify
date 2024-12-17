package com.yargisoft.birthify.ui.views.settings

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.data.sharedpreferences.UserConstants
import com.yargisoft.birthify.data.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.databinding.FragmentAuthAccountDetailsBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.ui.views.dialogs.ChangePasswordDialogFragment
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions.enableViewDisableLottie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountDetailsFragment : Fragment() {

    private var _binding: FragmentAuthAccountDetailsBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    @Inject lateinit var birthdayRepository: BirthdayRepository
    @Inject lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var credentialPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAuthAccountDetailsBinding.inflate(inflater, container, false)

        credentialPreferences = requireContext().getSharedPreferences(
            UserConstants.PREFS_USER,
            android.content.Context.MODE_PRIVATE
        )

        val lottieAnimationView = binding.threePointAnimation

        val name = credentialPreferences.getString("name", null)
        val email = credentialPreferences.getString("email", null)
        val recordedDate = credentialPreferences.getString("recordedDate", null)

        binding.accountNameEditText.setText(name ?: "N/A")
        binding.accountMailEditText.setText(email ?: "N/A")
        binding.accountRecordedDateEditText.setText(recordedDate ?: "N/A")

        binding.fabBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.changePasswordButton.setOnClickListener {
            val dialogFragment = ChangePasswordDialogFragment()
            dialogFragment.show(parentFragmentManager, "ChangePasswordDialog")
        }

        binding.resetPasswordButton.setOnClickListener {
            disableViewEnableLottie(lottieAnimationView, binding.root)

            AlertDialog.Builder(context)
                .setTitle(getString(R.string.reset_password_title_alert))
                .setMessage(getString(R.string.reset_password_alert))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->

                    authViewModel.resetPassword(userSharedPreferences.getUserCredentials().first.toString())

                    viewLifecycleOwner.lifecycleScope.launch {
                        var isLoadedEmitted = false

                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            authViewModel.isLoaded.collect { isLoaded ->
                                if (isLoaded && !isLoadedEmitted) {
                                    isLoadedEmitted = true

                                    val isSuccess = authViewModel.authSuccess.value
                                    val errorMessage = authViewModel.authError.value

                                    if (isSuccess) {
                                        Snackbar.make(
                                            binding.root,
                                            getString(R.string.reset_password_snackbar),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Snackbar.make(
                                            binding.root,
                                            "$errorMessage",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                    enableViewDisableLottie(lottieAnimationView, binding.root)
                                }
                            }
                        }
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    enableViewDisableLottie(lottieAnimationView, binding.root)
                }
                .show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
