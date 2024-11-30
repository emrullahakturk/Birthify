package com.yargisoft.birthify.views.guest_user_views

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
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentGuestDeletedBirthdayDetailBinding
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.views.auth_user_views.DeletedBirthdayDetailFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuestDeletedBirthdayDetailFragment : Fragment() {

    private lateinit var binding: FragmentGuestDeletedBirthdayDetailBinding
    private val guestBirthdayViewModel: GuestBirthdayViewModel by viewModels()
    private val deletedBirthday : DeletedBirthdayDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_deleted_birthday_detail, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.guestDeletedBirthdayDetailFragment, true)
            .build()



        //detayı gösterilmek istenen doğum gününü kutucuklara yansıtıyoruz
        binding.birthday= deletedBirthday.birthday

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        // Toolbardaki menü ikonu
        val toolbarMenuButton = binding.menuButtonToolbar

        binding.fabBackButton.setOnClickListener { findNavController().popBackStack() }


        binding.reSaveButton.setOnClickListener {

            GuestFrequentlyUsedFunctions.showConfirmationDialog(
                binding.root,
                requireContext(),
                guestBirthdayViewModel,
                deletedBirthday.birthday,
                binding.threePointAnimation,
                viewLifecycleOwner,
                "re_save",
                findNavController(),
                R.id.guestDeletedDetailToTrashBin,
                navOptions
            )

        }


        binding.permanentlyDeleteButton.setOnClickListener {
            GuestFrequentlyUsedFunctions.showConfirmationDialog(
                binding.root,
                requireContext(),
                guestBirthdayViewModel,
                deletedBirthday.birthday,
                binding.threePointAnimation,
                viewLifecycleOwner,
                "permanently",
                findNavController(),
                R.id.guestDeletedDetailToTrashBin,
                navOptions
            )
        }


        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            "GuestDeletedBirthdayDetail"
        )


        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.guestDeletedDetailToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.guestDeletedDetailToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.guestDeletedDetailToPastBirthdays)
                    true
                }
                else -> false
            }
        }






        return binding.root
    }

}