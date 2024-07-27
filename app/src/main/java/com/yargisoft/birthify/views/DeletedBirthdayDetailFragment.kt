package com.yargisoft.birthify.views

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentDeletedBirthdayDetailBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory

@Suppress("UNUSED_EXPRESSION")
class DeletedBirthdayDetailFragment : Fragment() {
    private lateinit var binding: FragmentDeletedBirthdayDetailBinding
    private lateinit var birthdayViewModel: BirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private val deletedBirthday : DeletedBirthdayDetailFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_deleted_birthday_detail , container, false)

        //Snackbar için view
         val  view = (context as Activity).findViewById<View>(android.R.id.content)


        //Birthday viewModel Tanımlama için gerekenler
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = BirthdayViewModelFactory(birthdayRepository)
        birthdayViewModel = ViewModelProvider(this,birthdayFactory)[BirthdayViewModel::class]


        //Auth ViewModel Tanımlama için gerekenler
        val authRepository = AuthRepository(requireContext())
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this,authFactory)[AuthViewModel::class]

        //detayı gösterilmek istenen doğum gününü kutucuklara yansıtıyoruz
        binding.birthday= deletedBirthday.birthday

        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayoutDeletedBirthdayDetail
        val navigationView: NavigationView = binding.navViewDetailDeletedBDay

        // ActionBarDrawerToggle ile Drawer'ı ActionBar ile senkronize etme
        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // NavigationView'deki öğeler için click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Menü öğelerine tıklandığında yapılacak işlemler
            when (menuItem.itemId) {
                R.id.labelBirthdays -> {
                    findNavController().navigate(R.id.deletedDetailToMain)
                }
                R.id.labelLogOut -> {
                    userSharedPreferences.clearUserSession()
                    authViewModel.logoutUser()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                R.id.labelTrashBin -> {
                    findNavController().navigate(R.id.deletedDetailToTrashBin)
                }
                R.id.labelSettings -> {
                    findNavController().navigate(R.id.deletedDetailToSettings)
                }
                R.id.labelProfile -> {
                    findNavController().navigate(R.id.deletedDetailToProfile)
                }
                else -> false
            }

            // Drawer'ı kapatmak için
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.fabDetailDeletedBirthday.setOnClickListener { findNavController().popBackStack() }

        // Toolbar üzerindeki menü ikonu ile menüyü açma
        binding.includeDeletedDetailBirthday.findViewById<View>(R.id.menuButtonToolbar).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        binding.reSaveDeletedBirthday.setOnClickListener {
            FrequentlyUsedFunctions.showReSaveConfirmationDialog(view,
                requireContext(),
                birthdayViewModel,
                binding.deletedBirthdayDetailLottieAnimation,
                 viewLifecycleOwner,
                 deletedBirthday.birthday,
                 findNavController()
            )
        }

        binding.permanentlyDeleteButton.setOnClickListener {
            FrequentlyUsedFunctions.showPermanentlyDeleteConfirmationDialog(
                requireView(),
                requireContext(),
                birthdayViewModel,
                binding.deletedBirthdayDetailLottieAnimation,
                viewLifecycleOwner,
                deletedBirthday.birthday,
                findNavController())
        }
        return binding.root
    }

}