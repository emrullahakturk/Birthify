package com.yargisoft.birthify.views.auth_user_views

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
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentAuthAddBirthdayBinding
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


class AddBirthdayFragment : Fragment() {
    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding : FragmentAuthAddBirthdayBinding
    private lateinit var userSharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_auth_add_birthday, container, false)
        val view = (context as Activity).findViewById<View>(android.R.id.content)

        userSharedPreferences = UserSharedPreferencesManager(requireContext())



        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayViewModelFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this,birthdayViewModelFactory)[UsersBirthdayViewModel::class.java]


        val authRepository = AuthRepository(requireContext())
        val authViewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this,authViewModelFactory)[AuthViewModel::class.java]




        binding.saveBirthdayButton.setOnClickListener {

            val name = binding.nameBirthdayEditText.text.toString()
            val birthdayDate = binding.birthdayDateEditText.text.toString()
            val note = binding.noteBirthdayEditText.text.toString()
            val userId =  userSharedPreferences.getUserId()
            val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()

            val bDay = Birthday(UUID.randomUUID().toString(), name, birthdayDate, recordedDate, note , userId )

            if (name.isNotEmpty() && birthdayDate.isNotEmpty() && note.isNotEmpty()) {
                usersBirthdayViewModel.saveBirthday(bDay)
                UserFrequentlyUsedFunctions.loadAndStateOperation(viewLifecycleOwner, usersBirthdayViewModel, binding.threePointAnimation, binding.root, findNavController(), R.id.addToMain)
            }
            else{
                Snackbar.make(view,"Please fill in all fields",Snackbar.LENGTH_SHORT).show()
            }
        }


        binding.birthdayDateEditText.setOnClickListener {
            UserFrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.fabBackButton.setOnClickListener { it.findNavController().popBackStack() }

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        // Toolbar üzerindeki menü ikonu ile menüyü açma
        val toolbarMenuIcon = binding.toolbar.findViewById<View>(R.id.menuButtonToolbar)



        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        UserFrequentlyUsedFunctions.drawerLayoutToggle(
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