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
import androidx.navigation.navOptions
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentAuthEditBirthdayBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory


class EditBirthdayFragment : Fragment() {

    private lateinit var binding : FragmentAuthEditBirthdayBinding
    private val editedBirthday : EditBirthdayFragmentArgs by navArgs()
    private lateinit var viewModel: UsersBirthdayViewModel
    private lateinit var repository: BirthdayRepository
    private lateinit var factory: UsersBirthdayViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_edit_birthday, container, false)

        repository = BirthdayRepository(requireContext())
        factory = UsersBirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[UsersBirthdayViewModel::class]

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
            viewModel.updateBirthdayToFirebase(updatedBirthday)

           UserFrequentlyUsedFunctions.loadAndStateOperation(
               viewLifecycleOwner,
               viewModel,
               binding.threePointAnimation,
               binding.root,
               findNavController(),
               R.id.editToMain,
               navOptions {  }
               )

        }

        binding.birthdayDateEditText.setOnClickListener {
            UserFrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.deleteBirthdayButton.setOnClickListener {
           UserFrequentlyUsedFunctions.showConfirmationDialog(
               binding.root,
               requireContext(),
               viewModel,
               editedBirthday.birthday,
               binding.threePointAnimation,
               viewLifecycleOwner,
               "soft_delete",
               findNavController(),
               action = R.id.editToMain,
               navOptions {  }
           )
        }

        binding.fabBackButton.setOnClickListener {
            findNavController().popBackStack()

        }

        return binding.root
    }



}