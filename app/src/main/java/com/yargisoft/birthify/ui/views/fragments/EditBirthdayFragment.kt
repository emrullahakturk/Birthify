package com.yargisoft.birthify.ui.views.fragments

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthEditBirthdayBinding
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.ui.views.dialogs.NotifyTimeBottomSheetDialogFragment
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.requestExactAlarmPermission
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.updateBirthdayReminder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditBirthdayFragment : Fragment() {

    private lateinit var binding: FragmentAuthEditBirthdayBinding
    private val editedBirthday: EditBirthdayFragmentArgs by navArgs()
    private val birthdayViewModel: BirthdayViewModel by viewModels()

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    private lateinit var navOptions: NavOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_edit_birthday, container, false)
        navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.birthdayEditFragment, true)
            .build()

        setupUI()
        setupListeners()

        return binding.root
    }

    // Kullanıcı arayüzünü ayarlama
    private fun setupUI() {
        binding.birthday = editedBirthday.birthday
    }

    // Dinleyicileri ayarlama
    private fun setupListeners() {
        setupNotifyTimePicker()
        setupUpdateButton()
        setupDatePicker()
        setupDeleteButton()
        setupBackButton()
    }

    // Bildirim zamanı seçici
    private fun setupNotifyTimePicker() {
        binding.notifyTimeEditText.setOnClickListener {
            val bottomSheet = NotifyTimeBottomSheetDialogFragment()
            bottomSheet.setOnOptionSelectedListener { selectedOption ->
                binding.notifyTimeEditText.setText(selectedOption)
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    // Doğum günü güncelleme butonu
    private fun setupUpdateButton() {
        binding.updateBirthdayButton.setOnClickListener {
            val updatedBirthday = getUpdatedBirthdayDetails()
            handleExactAlarmPermission(updatedBirthday)
        }
    }

    private fun getUpdatedBirthdayDetails() = editedBirthday.birthday.copy(
        name = binding.birthdayNameEditText.text.toString(),
        birthdayDate = binding.birthdayDateEditText.text.toString(),
        note = binding.birthdayNoteEditText.text.toString(),
        notifyDate = binding.notifyTimeEditText.text.toString()
    )

    private fun handleExactAlarmPermission(updatedBirthday: Birthday) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission(requireActivity())
        } else {
            updateBirthdayAndNavigate(updatedBirthday)
        }
    }

    private fun updateBirthdayAndNavigate(updatedBirthday: Birthday) {
        updateBirthdayReminder(updatedBirthday, requireContext())
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

    // Tarih seçici
    private fun setupDatePicker() {
        binding.birthdayDateEditText.setOnClickListener {
            FrequentlyUsedFunctions.showDatePickerDialog(requireContext(), binding.birthdayDateEditText)
        }
    }

    // Doğum günü silme butonu
    private fun setupDeleteButton() {
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
    }

    // Geri butonu
    private fun setupBackButton() {
        binding.fabBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
