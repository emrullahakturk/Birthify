package com.yargisoft.birthify.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthBirthdayDetailBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class BirthdayDetailFragment : Fragment() {

    private lateinit var binding : FragmentAuthBirthdayDetailBinding

    private val birthdayViewModel: BirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var birthdayRepository: BirthdayRepository
    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

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



        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.menuButtonToolbar

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        FrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
             toolbarMenuButton,
            requireActivity(),
            authViewModel,
            birthdayViewModel,
            birthdayRepository,
            userSharedPreferences,
         )


        binding.birthdayEditButton.setOnClickListener {
            val action = BirthdayDetailFragmentDirections.detailToEdit(
                detailedBirthday.birthday
            )
            findNavController().navigate(action, navOptions)
        }



        return binding.root
    }



}