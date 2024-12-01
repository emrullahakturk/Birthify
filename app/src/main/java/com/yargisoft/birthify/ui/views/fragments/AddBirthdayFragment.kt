package com.yargisoft.birthify.ui.views.fragments

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
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthAddBirthdayBinding
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.ui.views.dialogs.NotifyTimeBottomSheetDialogFragment
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.requestExactAlarmPermission
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.scheduleBirthdayReminder
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint

class AddBirthdayFragment : Fragment() {
    
  
    
    private lateinit var binding : FragmentAuthAddBirthdayBinding
    private val birthdayViewModel: BirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    
    @Inject 
    lateinit var birthdayRepository: BirthdayRepository
    
    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    @Inject lateinit var bottomSheet : NotifyTimeBottomSheetDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_auth_add_birthday, container, false)


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
                    birthdayViewModel.saveBirthday(bDay)
                    FrequentlyUsedFunctions.loadAndStateOperation(viewLifecycleOwner, birthdayViewModel, binding.threePointAnimation, binding.root, findNavController(), R.id.addToMain, navOptions {  })

                }

            }
            else{
                Snackbar.make(binding.root,"Please fill in all fields",Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.notificationTimeEditText.setOnClickListener{
            bottomSheet.setOnOptionSelectedListener { selectedOption ->
                binding.notifyDate = selectedOption
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)        }


        binding.birthdayDateEditText.setOnClickListener {
            FrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.fabBackButtonAdd.setOnClickListener { it.findNavController().popBackStack() }

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        // Toolbar üzerindeki menü ikonu ile menüyü açma
        val toolbarMenuButton = binding.menuButtonToolbar



        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        FrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            toolbarMenuButton,
            requireActivity(),
            authViewModel,
            birthdayViewModel,
            birthdayRepository,
            userSharedPreferences,
         )

        return binding.root
    }

}
