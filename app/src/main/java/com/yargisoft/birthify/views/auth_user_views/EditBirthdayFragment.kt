package com.yargisoft.birthify.views.auth_user_views

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
import com.yargisoft.birthify.databinding.FragmentAuthEditBirthdayBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


class EditBirthdayFragment : Fragment() {

    private lateinit var binding : FragmentAuthEditBirthdayBinding
    private val editedBirthday : EditBirthdayFragmentArgs by navArgs()
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var repository: BirthdayRepository
    private lateinit var factory: BirthdayViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_edit_birthday, container, false)

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
                name = binding.nameBirthdayEditText.text.toString(),
                birthdayDate = binding.birthdayDateEditText.text.toString(),
                note = binding.noteBirthdayEditText.text.toString()
            )

            viewModel.updateBirthday(updatedBirthday)

           FrequentlyUsedFunctions.loadAndStateOperation(
               viewLifecycleOwner,
               viewModel,
               binding.threePointAnimation,
               binding.root,
               findNavController(),
               R.id.editToMain
               )

        }

        binding.birthdayDateEditText.setOnClickListener {
            FrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.deleteBirthdayButton.setOnClickListener {
           FrequentlyUsedFunctions.showConfirmationDialog(
               binding.root,
               requireContext(),
               viewModel,
               editedBirthday.birthday,
               binding.threePointAnimation,
               viewLifecycleOwner,
               "soft_delete",
               findNavController(),
               action = R.id.editToMain
           )
        }

        binding.fabBackButton.setOnClickListener {
            findNavController().popBackStack()

        }

        // Inflate the layout for this fragment
        return binding.root
    }



}