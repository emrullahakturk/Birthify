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
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.databinding.FragmentAuthBirthdayDetailBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory

class BirthdayDetailFragment : Fragment() {

    private lateinit var binding : FragmentAuthBirthdayDetailBinding

    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private val detailedBirthday : BirthdayDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_auth_birthday_detail, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.birthdayDetailFragment, true)
            .build()

        binding.birthday = detailedBirthday.birthday
        binding.fabBackButtonDetail.setOnClickListener { findNavController().popBackStack() }

        userSharedPreferences = UserSharedPreferencesManager(requireContext())


        //Birthday viewModel Tanımlama için gerekenler
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this, birthdayFactory)[UsersBirthdayViewModel::class]


        //Auth ViewModel Tanımlama için gerekenler
        val authRepository = AuthRepository(requireContext())
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class]



        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbarUserDetail.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        UserFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            authViewModel,
            birthdayRepository,
            userSharedPreferences,
            "BirthdayDetail"
        )

        binding.toolbarUserDetail.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            findNavController().navigate(R.id.detailToAdd)
        }

        binding.birthdayEditButton.setOnClickListener {
            val action = BirthdayDetailFragmentDirections.detailToEdit(
                detailedBirthday.birthday
            )
            findNavController().navigate(action, navOptions)
        }



        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.birthdayDetailToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.detailToTrash)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.detailToPastBirthdays)
                    true
                }
                else -> false
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }



}