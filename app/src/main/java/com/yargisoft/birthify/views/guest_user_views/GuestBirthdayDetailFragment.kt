package com.yargisoft.birthify.views.guest_user_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentGuestBirthdayDetailBinding
import com.yargisoft.birthify.views.dialogs.NotifyTimeBottomSheetDialogFragment
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager

class GuestBirthdayDetailFragment : Fragment() {

    private lateinit var binding : FragmentGuestBirthdayDetailBinding
    private val detailedBirthday : GuestBirthdayDetailFragmentArgs by navArgs()
    private lateinit var guestRepository : GuestRepository
    private lateinit var userSharedPreferences : UserSharedPreferencesManager

    val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.guestBirthdayDetailFragment, true)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_birthday_detail, container, false)

        guestRepository = GuestRepository(requireContext())
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        binding.birthday = detailedBirthday.birthday
        binding.fabBackButtonDetail.setOnClickListener { findNavController().popBackStack() }
        binding.birthdayEditButton.setOnClickListener {
            val action = GuestBirthdayDetailFragmentDirections.guestDetailToEdit(
                detailedBirthday.birthday
            )
            findNavController().navigate(action,navOptions)
        }


        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.guestDetailToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.guestDetailToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.guestDetailToPastBirthdays)
                    true
                }
                else -> false
            }
        }

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.menuButtonToolbar

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            userSharedPreferences,
            "GuestBirthdayDetail"
        )



        return binding.root
    }




}