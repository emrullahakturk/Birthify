package com.yargisoft.birthify.views.auth_user_views

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
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.UserFrequentlyUsedFunctions.requestExactAlarmPermission
import com.yargisoft.birthify.databinding.FragmentAuthEditBirthdayBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.views.dialogs.NotifyTimeBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditBirthdayFragment : Fragment() {

    private lateinit var binding : FragmentAuthEditBirthdayBinding
    private val editedBirthday : EditBirthdayFragmentArgs by navArgs()
    private val usersBirthdayViewModel: UsersBirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var birthdayRepository: BirthdayRepository
    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_edit_birthday, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.birthdayEditFragment, true)
            .build()


        //editlenen doğum günü bilgilerini ekrana yansıtıyoruz
        binding.birthday = editedBirthday.birthday



        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.menuButtonToolbar

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
            "BirthdayEdit"
        )


        binding.notifyTimeEditText.setOnClickListener {
            val bottomSheet = NotifyTimeBottomSheetDialogFragment()
            bottomSheet.setOnOptionSelectedListener { selectedOption ->
                binding.notifyTimeEditText.setText(selectedOption)
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }




        //doğum günü update butonu
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
                UserFrequentlyUsedFunctions.updateBirthdayReminder(updatedBirthday,requireContext())
                usersBirthdayViewModel.updateBirthday(updatedBirthday)
                UserFrequentlyUsedFunctions.loadAndStateOperation(
                    viewLifecycleOwner,
                    usersBirthdayViewModel,
                    binding.threePointAnimation,
                    binding.root,
                    findNavController(),
                    R.id.editToMain,
                    navOptions
                )
            }
        }

        binding.birthdayDateEditText.setOnClickListener {
            UserFrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.deleteBirthdayButton.setOnClickListener {
           UserFrequentlyUsedFunctions.showConfirmationDialog(
               binding.root,
               requireContext(),
               usersBirthdayViewModel,
               editedBirthday.birthday,
               binding.threePointAnimation,
               viewLifecycleOwner,
               "soft_delete",
               findNavController(),
               action = R.id.editToMain,
               navOptions
           )
        }

        binding.fabBackButton.setOnClickListener {
            findNavController().popBackStack()

        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.editToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.editToTrash)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.editToPastBirthdays)
                    true
                }
                else -> false
            }
        }


        return binding.root
    }

}