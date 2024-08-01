package com.yargisoft.birthify.views.guest_user_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentAuthDeletedBirthdayDetailBinding
import com.yargisoft.birthify.databinding.FragmentGuestDeletedBirthdayDetailBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.GuestViewModelFactory
import com.yargisoft.birthify.views.auth_user_views.DeletedBirthdayDetailFragmentArgs


class GuestDeletedBirthdayDetailFragment : Fragment() {

    private lateinit var binding: FragmentGuestDeletedBirthdayDetailBinding
    private lateinit var guestBirthdayViewModel: GuestBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private val deletedBirthday : DeletedBirthdayDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_deleted_birthday_detail, container, false)



        //Guest Birthday ViewModel Tanımlama için gerekenler
        val guestRepository = GuestRepository(requireContext())
        val guestFactory = GuestViewModelFactory(guestRepository)
        guestBirthdayViewModel = ViewModelProvider(this, guestFactory)[GuestBirthdayViewModel::class]


        //Auth ViewModel Tanımlama için gerekenler
        val authRepository = AuthRepository(requireContext())
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this,authFactory)[AuthViewModel::class]

        //detayı gösterilmek istenen doğum gününü kutucuklara yansıtıyoruz
        binding.birthday= deletedBirthday.birthday

        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        // Toolbardaki menü ikonu
        val menuToolbarIcon: View = binding.toolbarDeletedDetail.findViewById(R.id.menuButtonToolbar)

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
                R.id.guestDeletedDetailToTrashBin
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
                R.id.guestDeletedDetailToTrashBin
            )
        }


        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            menuToolbarIcon,
            requireActivity(),
            guestRepository,
            userSharedPreferences,
            "GuestDeletedBirthdayDetail"
        )







        return binding.root
    }

}