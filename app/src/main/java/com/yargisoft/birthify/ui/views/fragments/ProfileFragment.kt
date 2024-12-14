package com.yargisoft.birthify.ui.views.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.AuthActivity
import com.yargisoft.birthify.FrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.FrequentlyUsedFunctions.enableViewDisableLottie
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthProfileBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.ui.views.dialogs.ChangePasswordDialogFragment
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.cancelBirthdayReminder
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentAuthProfileBinding
    private val birthdayViewModel: BirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_profile, container, false)

        setupDeleteAccount()
        setupLogout()
        setupChangePassword()
        setupAccountDetailsNavigation()
        setupDeleteAllBirthdays()
        setupForgotPassword()
        setupBackButton()

        return binding.root
    }

    // Hesap Silme İşlemi
    private fun setupDeleteAccount() {
        binding.deleteMyAccountCardView.setOnClickListener {
            val lottieAnimationView = binding.threePointAnimation
            disableViewEnableLottie(lottieAnimationView, binding.root)

            AlertDialog.Builder(context)
                .setTitle(getString(R.string.confirm_deletion_account_title))
                .setMessage(getString(R.string.confirm_deletion_account))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    performDeleteAccount(lottieAnimationView)
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                    enableViewDisableLottie(lottieAnimationView, binding.root)
                }
                .show()
        }
    }

    private fun performDeleteAccount(lottieAnimationView: LottieAnimationView) {
        birthdayViewModel.clearAllBirthdays()
        authViewModel.deleteUserAccount()

        viewLifecycleOwner.lifecycleScope.launch {
            var isLoadedEmitted = false
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.isLoaded.collect { isLoaded ->
                    if (isLoaded && !isLoadedEmitted) {
                        isLoadedEmitted = true
                        handleDeleteAccountResult(lottieAnimationView)
                    }
                }
            }
        }
    }

    private fun handleDeleteAccountResult(lottieAnimationView: LottieAnimationView) {
        val isSuccess = authViewModel.authSuccess.value
        val errorMessage = authViewModel.authError.value

        if (isSuccess) {
            logout(requireActivity())
        } else {
            Snackbar.make(binding.root, errorMessage ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()
        }
        enableViewDisableLottie(lottieAnimationView, binding.root)
    }

    // Çıkış İşlemi
    private fun setupLogout() {
        binding.logoutCardView.setOnClickListener {
            authViewModel.logoutUser()
            userSharedPreferences.clearUserSession()
            birthdayRepository.clearAllBirthdays()
            cancelAllReminders()
            logout(requireActivity())
        }
    }

    private fun cancelAllReminders() {
        birthdayViewModel.birthdayList.value?.forEach { birthday ->
            cancelBirthdayReminder(birthday.id, birthday.name, birthday.birthdayDate, requireContext())
        }
    }

    // Şifre Değiştirme İşlemi
    private fun setupChangePassword() {
        binding.changePasswordCardView.setOnClickListener {
            val dialogFragment = ChangePasswordDialogFragment()
            dialogFragment.show(parentFragmentManager, "ChangePasswordDialog")
        }
    }

    // Hesap Detayları Sayfasına Navigasyon
    private fun setupAccountDetailsNavigation() {
        binding.myAccountCardView.setOnClickListener {
            findNavController().navigate(R.id.profileToAccountDetails)
        }
    }

    // Tüm Doğum Günlerini Silme İşlemi
    private fun setupDeleteAllBirthdays() {
        binding.deleteAllBirthdaysCardView.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.confirm_deletion_all_birthdays_title))
                .setMessage(getString(R.string.confirm_deletion_all_birthdays))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    birthdayViewModel.clearAllBirthdays()
                    findNavController().navigate(R.id.profileToMainPage)
                }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }
    }

    // Şifreyi Unutma İşlemi
    private fun setupForgotPassword() {
        binding.forgotPasswordCardView.setOnClickListener {
            val lottieAnimationView = binding.threePointAnimation
            disableViewEnableLottie(lottieAnimationView, binding.root)

            AlertDialog.Builder(context)
                .setTitle(getString(R.string.reset_password_title_alert))
                .setMessage(getString(R.string.reset_password_alert))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    performForgotPassword(lottieAnimationView)
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    enableViewDisableLottie(lottieAnimationView, binding.root)
                }
                .show()
        }
    }

    private fun performForgotPassword(lottieAnimationView: LottieAnimationView) {
        authViewModel.resetPassword(userSharedPreferences.getUserCredentials().first ?: "")

        viewLifecycleOwner.lifecycleScope.launch {
            var isLoadedEmitted = false
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.isLoaded.collect { isLoaded ->
                    if (isLoaded && !isLoadedEmitted) {
                        isLoadedEmitted = true
                        handleForgotPasswordResult(lottieAnimationView)
                    }
                }
            }
        }
    }

    private fun handleForgotPasswordResult(lottieAnimationView: LottieAnimationView) {
        val isSuccess = authViewModel.authSuccess.value
        val errorMessage = authViewModel.authError.value

        if (isSuccess) {
            Snackbar.make(binding.root, getString(R.string.reset_password_snackbar), Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(binding.root, errorMessage ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()
        }
        enableViewDisableLottie(lottieAnimationView, binding.root)
    }

    // Geri Buton İşlemi
    private fun setupBackButton() {
        binding.fabBackButtonDetail.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun logout(activity: Activity) {
        userSharedPreferences.clearUserSession()
        val intent = Intent(activity, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }
}
