package com.yargisoft.birthify.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentBirthdayEditBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


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
        //val view = (context as Activity).findViewById<View>(android.R.id.content)


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

           FrequentlyUsedFunctions.loadAndStateOperation(
               viewLifecycleOwner,
               viewModel,
               binding.editBirthdayLottieAnimation,
               binding.root,
               findNavController(),
               R.id.editToMain
               )

        }

        binding.birthdayDateEditBirthdayTv.setOnClickListener {
            FrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditBirthdayTv)
        }

        binding.deleteBirthdayButton.setOnClickListener {
           FrequentlyUsedFunctions.showConfirmationDialog(
               binding.root,
               requireContext(),
               viewModel,
               editedBirthday.birthday,
               binding.editBirthdayLottieAnimation,
               viewLifecycleOwner,
               "soft_delete",
               findNavController(),
               action = R.id.editToMain
           )
        }

        binding.fabEditBirthday.setOnClickListener {
            findNavController().popBackStack()

        }

        // Inflate the layout for this fragment
        return binding.root
    }



}