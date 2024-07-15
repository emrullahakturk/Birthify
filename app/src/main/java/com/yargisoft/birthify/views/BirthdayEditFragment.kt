package com.yargisoft.birthify.views

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentBirthdayEditBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory
import java.util.Calendar


class BirthdayEditFragment : Fragment() {
    private lateinit var binding : FragmentBirthdayEditBinding
    private val editedBirthday : BirthdayEditFragmentArgs by navArgs()
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var repository: BirthdayRepository
    private lateinit var factory: BirthdayViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        repository = BirthdayRepository(requireContext())
        factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[BirthdayViewModel::class]

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_birthday_edit, container, false)

        binding.birthday = editedBirthday.birthday

        binding.updateBirthdayButton.setOnClickListener {
            val updatedBirthday = editedBirthday.birthday.copy(
                name = binding.nameEditBirthdayTv.text.toString(),
                birthdayDate = binding.birthdayDateEditBirthdayTv.text.toString(),
                note = binding.noteEditBirthdayTv.text.toString()
            )

            viewModel.updateBirthday(updatedBirthday) { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(context, "Birthday updated successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Failed to update birthday", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.birthdayDateEditBirthdayTv.setOnClickListener {
            showDatePickerDialog()
        }

        binding.deleteBirthdayButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        binding.fabEditBirthday.setOnClickListener {
            findNavController().popBackStack()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
            binding.birthdayDateEditBirthdayTv.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Birthday")
            .setMessage("Are you sure you want to delete this birthday?")
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.deleteBirthday(editedBirthday.birthday.id!!) { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(context, "Birthday deleted successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, "Failed to delete birthday", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}