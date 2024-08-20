package com.yargisoft.birthify.views.guest_user_views

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentGuestAddBirthdayBinding
import com.yargisoft.birthify.views.dialogs.NotifyTimeBottomSheetDialogFragment
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.GuestViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


class GuestAddBirthdayFragment : Fragment() {
    private lateinit var guestBirthdayViewModel: GuestBirthdayViewModel
    private lateinit var binding : FragmentGuestAddBirthdayBinding
    private lateinit var userSharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_add_birthday, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.guestAddBirthdayFragment, true)
            .build()

        val view = (context as Activity).findViewById<View>(android.R.id.content)

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        // Toolbar üzerindeki menü ikonu ile menüyü açma
        val toolbarMenuButton = binding.menuButtonToolbar





        userSharedPreferences = UserSharedPreferencesManager(requireContext())


        //Guest Birthday ViewModel Tanımlama için gerekenler
        val guestRepository = GuestRepository(requireContext())
        val guestFactory = GuestViewModelFactory(guestRepository)
        guestBirthdayViewModel = ViewModelProvider(this, guestFactory)[GuestBirthdayViewModel::class]



        binding.notificationTimeEditText.setOnClickListener {
            val bottomSheet = NotifyTimeBottomSheetDialogFragment()
            bottomSheet.setOnOptionSelectedListener { selectedOption ->
                binding.notifyDate = selectedOption
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.saveBirthdayButton.setOnClickListener {

            val name = binding.birthdayNameEditText.text.toString()
            val birthdayDate = binding.birthdayDateEditText.text.toString()
            val note = binding.birthdayNoteEditText.text.toString()
            val userId =  userSharedPreferences.getUserId()
            val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
            val notifyDate = binding.notificationTimeEditText.text.toString()

            val bDay = Birthday(UUID.randomUUID().toString(), name, birthdayDate, recordedDate, note , userId,notifyDate )

            if (name.isNotEmpty() && birthdayDate.isNotEmpty() && note.isNotEmpty()) {
                guestBirthdayViewModel.saveBirthday(bDay)
              GuestFrequentlyUsedFunctions.loadAndStateOperation(viewLifecycleOwner, binding.threePointAnimation, binding.root, findNavController(), R.id.guestAddToMain, navOptions)
            }
            else{
                Snackbar.make(view,"Please fill in all fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.birthdayDateEditText.setOnClickListener {
            GuestFrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }


         binding.fabBackButtonAdd.setOnClickListener { it.findNavController().popBackStack() }



        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            userSharedPreferences,
            "GuestAddBirthday"
        )

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.guestAddToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.guestAddToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.guestAddToPastBirthdays)
                    true
                }
                else -> false
            }
        }






        return binding.root
    }

}