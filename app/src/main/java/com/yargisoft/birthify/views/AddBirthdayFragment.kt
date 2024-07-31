package com.yargisoft.birthify.views

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


class AddBirthdayFragment : Fragment() {
    private lateinit var birthdayViewModel: BirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding : FragmentAddBirthdayBinding
    private lateinit var userSharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_birthday, container, false)
        val view = (context as Activity).findViewById<View>(android.R.id.content)

        userSharedPreferences = UserSharedPreferencesManager(requireContext())



        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayViewModelFactory = BirthdayViewModelFactory(birthdayRepository)
        birthdayViewModel = ViewModelProvider(this,birthdayViewModelFactory)[BirthdayViewModel::class.java]


        val authRepository = AuthRepository(requireContext())
        val authViewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this,authViewModelFactory)[AuthViewModel::class.java]




        binding.saveBirthdayButton.setOnClickListener {

            val name = binding.nameAddBirthdayEditText.text.toString()
            val birthdayDate = binding.birthdayDateEditText.text.toString()
            val note = binding.noteAddBirthdayEditText.text.toString()
            val userId =  userSharedPreferences.getUserId()
            val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()

            val bDay = Birthday(UUID.randomUUID().toString(), name, birthdayDate, recordedDate, note , userId )

            if (name.isNotEmpty() && birthdayDate.isNotEmpty() && note.isNotEmpty()) {
                birthdayViewModel.saveBirthday(bDay)
                FrequentlyUsedFunctions.loadAndStateOperation(viewLifecycleOwner, birthdayViewModel, binding.addBirthdayLottieAnimation, binding.root, findNavController(), R.id.addToMain)
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
        // Toolbar üzerindeki menü ikonu ile menüyü açma
        val toolbarMenuIcon = binding.includeAddBirthday.findViewById<View>(R.id.menuButtonToolbar)



        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        FrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuIcon,
            requireActivity(),
            authViewModel,
            birthdayRepository,
            userSharedPreferences,
            "AddBirthday"
        )


        return binding.root
    }

}
