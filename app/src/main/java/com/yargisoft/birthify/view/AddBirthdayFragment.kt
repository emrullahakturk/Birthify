package com.yargisoft.birthify.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentAddBirthdayBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.viewmodels.BirthdayViewModel


class AddBirthdayFragment : Fragment() {
    private lateinit var viewModel: BirthdayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddBirthdayBinding.inflate(inflater, container, false)

        val repository = BirthdayRepository()
         viewModel = ViewModelProvider(this).get(BirthdayViewModel::class.java)



        binding.saveBirthdayButton.setOnClickListener {
            val name = binding.nameAddBirthdayEditText.text.toString()
            val birthdayDate = binding.birthdayDateEditText.text.toString()
            val note = binding.noteAddBirthdayEditText.text.toString()

            if (name.isNotEmpty() && birthdayDate.isNotEmpty() && note.isNotEmpty()) {
                viewModel.saveBirthday(name, birthdayDate,  note)
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabSaveBirthday.setOnClickListener { it.findNavController().popBackStack() }

        viewModel.saveBirthdayState.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Birthday saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save birthday", Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }
}
