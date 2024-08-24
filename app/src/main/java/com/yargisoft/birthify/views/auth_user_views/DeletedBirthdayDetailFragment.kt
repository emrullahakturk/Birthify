package com.yargisoft.birthify.views.auth_user_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentAuthDeletedBirthdayDetailBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory

class DeletedBirthdayDetailFragment : Fragment() {
    private lateinit var binding: FragmentAuthDeletedBirthdayDetailBinding
    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private val deletedBirthday : DeletedBirthdayDetailFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_deleted_birthday_detail , container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.deletedBirthdayDetailFragment, true)
            .build()

        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        //Birthday viewModel Tanımlama için gerekenler
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this,birthdayFactory)[UsersBirthdayViewModel::class]


        //Auth ViewModel Tanımlama için gerekenler
        val authRepository = AuthRepository(userSharedPreferences.preferences)
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this,authFactory)[AuthViewModel::class]

        //detayı gösterilmek istenen doğum gününü kutucuklara yansıtıyoruz
        binding.birthday= deletedBirthday.birthday


        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        // Toolbardaki menü ikonu
        val toolbarMenuButton = binding.menuButtonToolbar

        binding.fabBackButton.setOnClickListener { findNavController().popBackStack() }




        binding.reSaveButton.setOnClickListener {
            UserFrequentlyUsedFunctions.showConfirmationDialog(
                binding.root,
                requireContext(),
                usersBirthdayViewModel,
                deletedBirthday.birthday,
                binding.threePointAnimation,
                viewLifecycleOwner,
                "re_save",
                findNavController(),
                R.id.deletedDetailToTrashBin,
                navOptions
            )
        }


        binding.permanentlyDeleteButton.setOnClickListener {
            UserFrequentlyUsedFunctions.showConfirmationDialog(
                binding.root,
                requireContext(),
                usersBirthdayViewModel,
                deletedBirthday.birthday,
                binding.threePointAnimation,
                viewLifecycleOwner,
                "permanently",
                findNavController(),
                R.id.deletedDetailToTrashBin,
                navOptions
                )
        }


        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
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
            "DeletedBirthdayDetail"
        )


        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.deletedDetailToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.deletedDetailToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.deletedDetailToPastBirthdays)
                    true
                }
                else -> false
            }
        }


        return binding.root
    }

}