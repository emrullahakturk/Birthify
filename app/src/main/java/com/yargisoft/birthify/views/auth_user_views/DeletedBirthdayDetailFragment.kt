package com.yargisoft.birthify.views.auth_user_views

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
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.databinding.FragmentAuthDeletedBirthdayDetailBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class DeletedBirthdayDetailFragment : Fragment() {
    private lateinit var binding: FragmentAuthDeletedBirthdayDetailBinding
    private val usersBirthdayViewModel: UsersBirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    private val deletedBirthday : DeletedBirthdayDetailFragmentArgs by navArgs()


    @Inject
    lateinit var birthdayRepository:BirthdayRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_deleted_birthday_detail , container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.deletedBirthdayDetailFragment, true)
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