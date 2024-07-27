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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentAddBirthdayBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


@Suppress("UNUSED_EXPRESSION")
class AddBirthdayFragment : Fragment() {
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var binding : FragmentAddBirthdayBinding
    private lateinit var sharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_birthday, container, false)


        sharedPreferences = UserSharedPreferencesManager(requireContext())

        val repository = BirthdayRepository(requireContext())
        val factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[BirthdayViewModel::class.java]

        binding.saveBirthdayButton.setOnClickListener {

            val name = binding.nameAddBirthdayEditText.text.toString()
            val birthdayDate = binding.birthdayDateEditText.text.toString()
            val note = binding.noteAddBirthdayEditText.text.toString()
            val userId =  sharedPreferences.getUserId()


            val view = (context as Activity).findViewById<View>(android.R.id.content)

            if (name.isNotEmpty() && birthdayDate.isNotEmpty() && note.isNotEmpty()) {
                binding.addBirthdayTopLayout.visibility = View.INVISIBLE
                binding.addBirthdayConsLayout.setBackgroundResource(R.drawable.welcome_background)
                binding.addBirthdayLottieAnimation.visibility = View.VISIBLE
                binding.addBirthdayLottieAnimation.playAnimation()

                viewModel.saveBirthday(name, birthdayDate, note, userId = userId)

                FrequentlyUsedFunctions.addBirthdayValidation(viewLifecycleOwner,
                    viewModel,
                    binding.addBirthdayLottieAnimation,
                    binding.root,
                    findNavController(),
                    binding.addBirthdayTopLayout,
                    binding.addBirthdayConsLayout)

            }
            else{
                Snackbar.make(view,"Please fill in all fields",Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.birthdayDateEditText.setOnClickListener {
            FrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.fabSaveBirthday.setOnClickListener { it.findNavController().popBackStack() }

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.addBirthdayDrawerLayout
        val navigationView: NavigationView = binding.navViewAddBDay


        // ActionBarDrawerToggle ile Drawer'ı ActionBar ile senkronize etme
        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // NavigationView'deki öğeler için click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Menü öğelerine tıklandığında yapılacak işlemler
            when (menuItem.itemId) {
                R.id.labelBirthdays -> {
                    findNavController().navigate(R.id.mainToMain)
                }
                R.id.labelLogOut -> {
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                R.id.labelTrashBin -> {
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                R.id.labelSettings -> {
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                R.id.labelProfile -> {
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                else -> false
            }

            // Drawer'ı kapatmak için
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Toolbar üzerindeki menü ikonu ile menüyü açma
        binding.includeAddBirthday.findViewById<View>(R.id.menuButtonToolbar).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return binding.root
    }

}
