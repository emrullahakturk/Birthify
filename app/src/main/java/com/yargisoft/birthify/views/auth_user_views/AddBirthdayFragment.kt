package com.yargisoft.birthify.views.auth_user_views

import android.app.Activity
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.UserFrequentlyUsedFunctions.requestExactAlarmPermission
import com.yargisoft.birthify.UserFrequentlyUsedFunctions.scheduleBirthdayReminder
import com.yargisoft.birthify.databinding.FragmentAuthAddBirthdayBinding
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.views.dialogs.NotifyTimeBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint

class AddBirthdayFragment : Fragment() {
    
  
    
    private lateinit var binding : FragmentAuthAddBirthdayBinding
    private val usersBirthdayViewModel: UsersBirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    
    @Inject 
    lateinit var birthdayRepository:BirthdayRepository
    
    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_auth_add_birthday, container, false)
        val view = (context as Activity).findViewById<View>(android.R.id.content)



        binding.saveBirthdayButton.setOnClickListener {

            val name = binding.birthdayNameEditText.text.toString()
            val birthdayDate = binding.birthdayDateEditText.text.toString()
            val note = binding.birthdayNoteEditText.text.toString()
            val userId =  userSharedPreferences.getUserId()
            val notifyDate = binding.notificationTimeEditText.text.toString()
            val recordedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()

            val bDay = Birthday(UUID.randomUUID().toString(), name, birthdayDate, recordedDate, note , userId, notifyDate)

            if (name.isNotEmpty() && birthdayDate.isNotEmpty() && note.isNotEmpty()) {


                val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    // İzin talep etmeniz gerekecek
                    requestExactAlarmPermission(requireActivity())
                } else {
                    // İzin zaten verilmiş, alarm ayarlayabilirsiniz
                    scheduleBirthdayReminder(bDay.id, bDay.name, birthdayDate, notifyDate,requireContext())
                    usersBirthdayViewModel.saveBirthday(bDay)
                    UserFrequentlyUsedFunctions.loadAndStateOperation(viewLifecycleOwner, usersBirthdayViewModel, binding.threePointAnimation, binding.root, findNavController(), R.id.addToMain, navOptions {  })

                }

            }
            else{
                Snackbar.make(view,"Please fill in all fields",Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.notificationTimeEditText.setOnClickListener{
            val bottomSheet = NotifyTimeBottomSheetDialogFragment()
            bottomSheet.setOnOptionSelectedListener { selectedOption ->
                binding.notifyDate = selectedOption
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)        }


        binding.birthdayDateEditText.setOnClickListener {
            UserFrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.fabBackButtonAdd.setOnClickListener { it.findNavController().popBackStack() }

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        // Toolbar üzerindeki menü ikonu ile menüyü açma
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
            "AddBirthday"
        )

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.addToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.addToTrash)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.addToPastBirthdays)
                    true
                }
                else -> false
            }
        }
        return binding.root
    }

}
