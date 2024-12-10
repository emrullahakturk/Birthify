package com.yargisoft.birthify.ui.views.fragments

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthEditBirthdayBinding
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.ui.views.dialogs.NotifyTimeBottomSheetDialogFragment
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.requestExactAlarmPermission
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.updateBirthdayReminder
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditBirthdayFragment : Fragment() {

    private lateinit var binding : FragmentAuthEditBirthdayBinding
    private val editedBirthday : EditBirthdayFragmentArgs by navArgs()
    private val birthdayViewModel: BirthdayViewModel by viewModels()

    @Inject
    lateinit var birthdayRepository: BirthdayRepository
    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    @RequiresApi(Build.VERSION_CODES.S)
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
                updateBirthdayReminder(updatedBirthday,requireContext())
                birthdayViewModel.updateBirthday(updatedBirthday)
                FrequentlyUsedFunctions.loadAndStateOperation(
                    viewLifecycleOwner,
                    birthdayViewModel,
                    binding.threePointAnimation,
                    binding.root,
                    findNavController(),
                    R.id.editToMain,
                    navOptions
                )
            }
        }

        binding.birthdayDateEditText.setOnClickListener {
            FrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.deleteBirthdayButton.setOnClickListener {
           FrequentlyUsedFunctions.showConfirmationDialog(
               binding.root,
               requireContext(),
               birthdayViewModel,
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



        return binding.root
    }

}