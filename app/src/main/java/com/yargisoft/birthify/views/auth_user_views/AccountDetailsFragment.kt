package com.yargisoft.birthify.views.auth_user_views

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.UserFrequentlyUsedFunctions.disableViewEnableLottie
import com.yargisoft.birthify.UserFrequentlyUsedFunctions.enableViewDisableLottie
import com.yargisoft.birthify.databinding.FragmentAuthAccountDetailsBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory
import com.yargisoft.birthify.views.dialogs.ChangePasswordDialogFragment
import kotlinx.coroutines.launch


class AccountDetailsFragment : Fragment() {


    private lateinit var binding: FragmentAuthAccountDetailsBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var credentialPreferences: SharedPreferences
    private lateinit var authViewModelFactory: AuthViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_auth_account_details,container,false)

        userSharedPreferences = UserSharedPreferencesManager(requireContext())
        credentialPreferences = requireContext().getSharedPreferences("user_credentials", Context.MODE_PRIVATE)


        authRepository = AuthRepository(userSharedPreferences.preferences,requireContext())
        authViewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authViewModelFactory)[AuthViewModel::class]

        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this, birthdayFactory)[UsersBirthdayViewModel::class]

        val lottieAnimationView =  binding.threePointAnimation


        val name = credentialPreferences.getString("name",null)
        val email = credentialPreferences.getString("email",null)
        val recordedDate = credentialPreferences.getString("recordedDate",null)

        Log.e("tagımıs","credent$name")

        binding.name = name ?: "N/A"
        binding.mail =  email ?: "N/A"
        binding.recordedDate = recordedDate ?: "N/A"




        UserFrequentlyUsedFunctions.drawerLayoutToggle(
            binding.drawerLayoutDetail,
            binding.navigationViewDetails,
            findNavController(),
            binding.menuButtonToolbarDetail,
            requireActivity(),
            authViewModel,
            usersBirthdayViewModel,
            birthdayRepository,
            userSharedPreferences,
            "MyAccount"
        )


        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.accountDetailsToMain)
                    true
                }
                R.id.bottomNavTrashBin -> {
                    findNavController().navigate(R.id.accountDetailsToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.accountDetailsToPastBirthdays)
                    true
                }
                else -> false
            }
        }

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

        return binding.root
    }


}