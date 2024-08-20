package com.yargisoft.birthify.views.guest_user_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentGuestEditBirthdayBinding
import com.yargisoft.birthify.dialogs.NotifyTimeBottomSheetDialogFragment
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.GuestViewModelFactory

class GuestEditBirthdayFragment : Fragment() {

    private lateinit var binding : FragmentGuestEditBirthdayBinding
    private val editedBirthday : GuestEditBirthdayFragmentArgs by navArgs()
    private lateinit var guestBirthdayViewModel: GuestBirthdayViewModel
    private lateinit var guestRepository: GuestRepository
    private lateinit var guestFactory: GuestViewModelFactory
    private lateinit var userSharedPreferences: UserSharedPreferencesManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_guest_edit_birthday, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.guestEditBirthdayFragment, true)
            .build()

        //Guest Birthday ViewModel Tanımlama için gerekenler
         guestRepository = GuestRepository(requireContext())
         guestFactory = GuestViewModelFactory(guestRepository)
         guestBirthdayViewModel = ViewModelProvider(this, guestFactory)[GuestBirthdayViewModel::class]

        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())


        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbarGuestEditBirthday.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            userSharedPreferences,
            "GuestBirthdayEdit"
        )


        //editlenen doğum günü bilgilerini ekrana yansıtıyoruz
        binding.birthday = editedBirthday.birthday


        binding.deleteBirthdayButton.setOnClickListener {
          GuestFrequentlyUsedFunctions.showConfirmationDialog(
                binding.root,
                requireContext(),
                guestBirthdayViewModel,
                editedBirthday.birthday,
                binding.threePointAnimation,
                viewLifecycleOwner,
                "soft_delete",
                findNavController(),
                action = R.id.guestEditToMain,
                navOptions
            )
        }

        binding.updateBirthdayButton.setOnClickListener {

            val updatedBirthday = editedBirthday.birthday.copy(
                name = binding.birthdayNameEditText.text.toString(),
                birthdayDate = binding.birthdayDateEditText.text.toString(),
                note = binding.birthdayNoteEditText.text.toString(),
                notifyDate = binding.notifyTimeEditText.text.toString()
            )

            guestBirthdayViewModel.updateBirthday(updatedBirthday)

            GuestFrequentlyUsedFunctions.loadAndStateOperation(
                viewLifecycleOwner,
                binding.threePointAnimation,
                binding.root,
                findNavController(),
                R.id.guestEditToMain,
                navOptions
            )

        }

        binding.notifyTimeEditText.setOnClickListener {
            val bottomSheet = NotifyTimeBottomSheetDialogFragment()
            bottomSheet.setOnOptionSelectedListener { selectedOption ->
                binding.notifyTimeEditText.setText(selectedOption)
                // Seçilen değeri burada kullanabilirsin
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
        binding.birthdayDateEditText.setOnClickListener {
            GuestFrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.fabBackButton.setOnClickListener {
            findNavController().popBackStack()

        }
        binding.toolbarGuestEditBirthday.findViewById<View>(R.id.menuButtonToolbar).setOnClickListener {
                GuestFrequentlyUsedFunctions.drawerLayoutToggle(
                    drawerLayout,
                    navigationView,
                    findNavController(),
                    toolbarMenuButton,
                    requireActivity(),
                    userSharedPreferences,
                    "GuestMainPage"
                    )
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.guestEditToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.guestEditToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.guestEditToPastBirthdays)
                    true
                }
                else -> false
            }
        }


        return binding.root
    }

}