package com.yargisoft.birthify.views

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentDeletedBirthdayDetailBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory
import kotlinx.coroutines.launch

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

        // Toolbar üzerindeki menü ikonu ile menüyü açma
        binding.includeDeletedDetailBirthday.findViewById<View>(R.id.menuButtonToolbar).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        binding.reSaveDeletedBirthday.setOnClickListener {
            showReSaveConfirmationDialog(view)
        }

        binding.permanentlyDeleteButton.setOnClickListener {
            showDeleteConfirmationDialog(view)
        }


        return binding.root
    }

    private fun showReSaveConfirmationDialog(view:View) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Re Saving")
            .setMessage("Are you sure you want to re save this birthday?")
            .setPositiveButton("Yes") { _, _ ->

                birthdayViewModel.reSaveDeletedBirthday(deletedBirthday.birthday.id,deletedBirthday.birthday)

                binding.deletedBirthdayDetailLottieAnimation.playAnimation()
                binding.deletedBirthdayDetailLottieAnimation.visibility = View.VISIBLE
                setViewAndChildrenEnabled(requireView(),false)

                Handler(Looper.getMainLooper()).postDelayed({
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            birthdayViewModel.isLoading.collect { isLoading ->
                                if(!isLoading){
                                    //animasyonu durdurup view'i visible yapıyoruz
                                    binding.deletedBirthdayDetailLottieAnimation.cancelAnimation()
                                    binding.deletedBirthdayDetailLottieAnimation.visibility = View.INVISIBLE
                                    setViewAndChildrenEnabled(requireView(),true)
                                }
                            }
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                            birthdayViewModel.reSaveDeletedBirthdayState.collect{ isResaved->
                                if (isResaved){

                                    Snackbar.make(view,"Birthday re saved successfully", Snackbar.LENGTH_SHORT).show()
                                    findNavController().navigateUp()
                                }else{
                                    Snackbar.make(view,"Failed to re save birthday",Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },3000)
            }
            .setNegativeButton("No"){_,_->
                //animasyonu durdurup view'i visible yapıyoruz
                binding.deletedBirthdayDetailLottieAnimation.cancelAnimation()
                binding.deletedBirthdayDetailLottieAnimation.visibility = View.INVISIBLE
                setViewAndChildrenEnabled(requireView(),true)
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(view:View) {

        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deleting Permanently")
            .setMessage("Are you sure you want to permanently delete this birthday?")
            .setPositiveButton("Yes") { _, _ ->

                birthdayViewModel.deleteBirthdayPermanently(deletedBirthday.birthday.id)

                binding.deletedBirthdayDetailLottieAnimation.playAnimation()
                binding.deletedBirthdayDetailLottieAnimation.visibility = View.VISIBLE
                setViewAndChildrenEnabled(requireView(),false)

                Handler(Looper.getMainLooper()).postDelayed({
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            birthdayViewModel.isLoading.collect { isLoading ->
                                if(!isLoading){
                                    //animasyonu durdurup view'i visible yapıyoruz
                                    binding.deletedBirthdayDetailLottieAnimation.cancelAnimation()
                                    binding.deletedBirthdayDetailLottieAnimation.visibility = View.INVISIBLE
                                    setViewAndChildrenEnabled(requireView(),true)
                                }
                            }
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                            birthdayViewModel.permanentlyDeleteBirthdayState.collect{ isResaved->
                                if (isResaved){
                                    Snackbar.make(view,"Birthday permanently deleted successfully", Snackbar.LENGTH_SHORT).show()
                                    findNavController().navigateUp()
                                }else{

                                    Snackbar.make(view,"Failed to permanently delete birthday",Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }


                },3000)

            }
            .setNegativeButton("No"){_,_->
                //animasyonu durdurup view'i visible yapıyoruz
                binding.deletedBirthdayDetailLottieAnimation.cancelAnimation()
                binding.deletedBirthdayDetailLottieAnimation.visibility = View.INVISIBLE
                setViewAndChildrenEnabled(requireView(),true)
            }
            .show()
    }


    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setViewAndChildrenEnabled(view.getChildAt(i), enabled)
            }
        }
    }
}