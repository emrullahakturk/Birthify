package com.yargisoft.birthify.views.auth_user_views

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.UserFrequentlyUsedFunctions.cancelBirthdayReminder
import com.yargisoft.birthify.UserFrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.UserFrequentlyUsedFunctions.enableViewDisableLottie
import com.yargisoft.birthify.databinding.FragmentAuthProfileBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory
import com.yargisoft.birthify.views.dialogs.ChangePasswordDialogFragment
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentAuthProfileBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authViewModelFactory: AuthViewModelFactory
    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_profile, container, false)

        val lottieAnimationView = binding.threePointAnimation

        userSharedPreferences = UserSharedPreferencesManager(requireContext())


        val authRepository = AuthRepository(userSharedPreferences.preferences,requireContext())
        authViewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authViewModelFactory)[AuthViewModel::class]

        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this, birthdayFactory)[UsersBirthdayViewModel::class]

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.menuButtonToolbar

        UserFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            authViewModel,
            usersBirthdayViewModel,
            birthdayRepository,
            userSharedPreferences,
            "Profile"
        )

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.settingsToMainPage)
                    true
                }
                R.id.bottomNavTrashBin -> {
                    findNavController().navigate(R.id.settingsToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.settingsToPastBirthdays)
                    true
                }
                else -> false
            }
        }

        binding.deleteMyAccountCardView.setOnClickListener {
            disableViewEnableLottie(lottieAnimationView, binding.root)

            AlertDialog.Builder(context)
                .setTitle(getString(R.string.confirm_deletion_account_title))
                .setMessage(getString(R.string.confirm_deletion_account))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->

                    usersBirthdayViewModel.clearAllBirthdays()
                    authViewModel.deleteUserAccount()

                    viewLifecycleOwner.lifecycleScope.launch {
                        var isLoadedEmitted = false

                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            authViewModel.isLoaded.collect { isLoaded ->
                                if (isLoaded && !isLoadedEmitted) {
                                    isLoadedEmitted = true

                                    val isSuccess = authViewModel.authSuccess.value
                                    val errorMessage = authViewModel.authError.value

                                    if (isSuccess) {
                                        UserFrequentlyUsedFunctions.logout(requireActivity())
                                    } else {
                                        Snackbar.make(binding.root, "$errorMessage", Snackbar.LENGTH_SHORT).show()
                                    }
                                    enableViewDisableLottie(lottieAnimationView, binding.root)
                                }
                            }
                        }
                    }
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                    enableViewDisableLottie(lottieAnimationView,binding.root)
                }
                .show()
        }

        binding.logoutCardView.setOnClickListener {
            authViewModel.logoutUser()
            userSharedPreferences.clearUserSession()
            birthdayRepository.clearBirthdays()
            birthdayRepository.clearDeletedBirthdays()
            birthdayRepository.clearPastBirthdays()
            usersBirthdayViewModel.birthdayList.value?.forEach { birthday ->
                cancelBirthdayReminder(birthday.id,birthday.name,birthday.birthdayDate,requireContext())
            }
            UserFrequentlyUsedFunctions.logout(requireActivity())
        }


        binding.changePasswordCardView.setOnClickListener {
            val dialogFragment = ChangePasswordDialogFragment()
            dialogFragment.show(parentFragmentManager, "ChangePasswordDialog")
        }

        binding.myAccountCardView.setOnClickListener {
            findNavController().navigate(R.id.profileToAccountDetails)
        }


        binding.deleteAllBirthdaysCardView.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.confirm_deletion_all_birthdays_title))
                .setMessage(getString(R.string.confirm_deletion_all_birthdays))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    usersBirthdayViewModel.clearAllBirthdays()
                    findNavController().navigate(R.id.settingsToMainPage)
                }
                .setNegativeButton(getString(R.string.no)) { _, _ -> }
                .show()
        }

        binding.forgotPasswordCardView.setOnClickListener {

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
                                        Snackbar.make(binding.root, getString(R.string.reset_password_snackbar), Snackbar.LENGTH_SHORT).show()

                                    } else {
                                        Snackbar.make(binding.root, "$errorMessage", Snackbar.LENGTH_SHORT).show()
                                    }
                                    enableViewDisableLottie(lottieAnimationView, binding.root)
                                }
                            }
                        }
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    enableViewDisableLottie(lottieAnimationView,binding.root)
                }
                .show()
        }

        binding.fabBackButtonDetail.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}