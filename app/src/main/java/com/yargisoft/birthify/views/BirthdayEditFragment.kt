package com.yargisoft.birthify.views

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentBirthdayEditBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory
import kotlinx.coroutines.launch
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
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_birthday_edit, container, false)

        repository = BirthdayRepository(requireContext())
        factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[BirthdayViewModel::class]

        //Snackbar için view tanımlaması
        val view = (context as Activity).findViewById<View>(android.R.id.content)


        //editlenen doğum günü bilgilerini ekrana yansıtıyoruz
        binding.birthday = editedBirthday.birthday


        //doğum günü update butonu
        binding.updateBirthdayButton.setOnClickListener {

            val updatedBirthday = editedBirthday.birthday.copy(
                name = binding.nameEditBirthdayTv.text.toString(),
                birthdayDate = binding.birthdayDateEditBirthdayTv.text.toString(),
                note = binding.noteEditBirthdayTv.text.toString()
            )
            viewModel.updateBirthday(updatedBirthday)

            setViewAndChildrenEnabled(requireView(), false)
            binding.editBirthdayLottieAnimation.visibility = View.VISIBLE
            binding.editBirthdayLottieAnimation.playAnimation()

            Handler(Looper.getMainLooper()).postDelayed({
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                        viewModel.updateBirthdayState.collect{isUpdated->
                            if(isUpdated){
                                Snackbar.make(view,"Birthday updated successfully", LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }else{
                                Snackbar.make(view,"Failed to update the birthday", LENGTH_SHORT).show()
                            }
                        }
                    }
                    setViewAndChildrenEnabled(requireView(), true)
                    binding.editBirthdayLottieAnimation.cancelAnimation()
                    binding.editBirthdayLottieAnimation.visibility = View.INVISIBLE

                }
            },3000)


        }

        binding.birthdayDateEditBirthdayTv.setOnClickListener {
            showDatePickerDialog()
        }

        binding.deleteBirthdayButton.setOnClickListener {
            showDeleteConfirmationDialog(view)
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

    private fun showDeleteConfirmationDialog(view: View) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this birthday?")
            .setPositiveButton("Yes") { _, _ ->

                viewModel.deleteBirthday(editedBirthday.birthday.id, editedBirthday.birthday)

                setViewAndChildrenEnabled(requireView(), false)
                binding.editBirthdayLottieAnimation.playAnimation()
                binding.editBirthdayLottieAnimation.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    viewLifecycleOwner.lifecycleScope.launch{
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.deleteBirthdayState.collect{isDeleted ->
                                if(isDeleted){
                                    findNavController().navigateUp()
                                    Snackbar.make(view,"Birthday deleted successfully", LENGTH_SHORT).show()
                                }else{
                                    Snackbar.make(view,"Failed to delete birthday", LENGTH_SHORT).show()
                                }

                            }
                        }
                        setViewAndChildrenEnabled(requireView(), true)
                        binding.editBirthdayLottieAnimation.cancelAnimation()
                        binding.editBirthdayLottieAnimation.visibility = View.INVISIBLE
                    }
                },3000)

            }
            .setNegativeButton("No",null)
            .show()
    }

    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setViewAndChildrenEnabled(view.getChildAt(i), enabled)
            }
        }
    }
}