package com.yargisoft.birthify.views.guest_user_views

import android.app.AlarmManager
import android.content.Context
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
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions.requestExactAlarmPermission
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions.updateBirthdayReminder
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentGuestEditBirthdayBinding
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.views.dialogs.NotifyTimeBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuestEditBirthdayFragment : Fragment() {

    private lateinit var binding : FragmentGuestEditBirthdayBinding
    private val editedBirthday : GuestEditBirthdayFragmentArgs by navArgs()
    private val guestBirthdayViewModel: GuestBirthdayViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_guest_edit_birthday, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.guestEditBirthdayFragment, true)
            .build()


        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.menuButtonToolbar

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
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



            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // İzin talep etmeniz gerekecek
                requestExactAlarmPermission(requireActivity())
            } else {
                // İzin zaten verilmiş, alarm ayarlayabilirsiniz
                updateBirthdayReminder(updatedBirthday,requireContext())
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