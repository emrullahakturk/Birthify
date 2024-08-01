package com.yargisoft.birthify.views.guest_user_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentGuestEditBirthdayBinding
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.GuestViewModelFactory

class GuestEditBirthdayFragment : Fragment() {

    private lateinit var binding : FragmentGuestEditBirthdayBinding
    private val editedBirthday : GuestEditBirthdayFragmentArgs by navArgs()
    private lateinit var guestBirthdayViewModel: GuestBirthdayViewModel
    private lateinit var guestRepository: GuestRepository
    private lateinit var guestFactory: GuestViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_guest_edit_birthday, container, false)


        //Guest Birthday ViewModel Tanımlama için gerekenler
         guestRepository = GuestRepository(requireContext())
         guestFactory = GuestViewModelFactory(guestRepository)
         guestBirthdayViewModel = ViewModelProvider(this, guestFactory)[GuestBirthdayViewModel::class]


        //editlenen doğum günü bilgilerini ekrana yansıtıyoruz
        binding.birthday = editedBirthday.birthday


        binding.updateBirthdayButton.setOnClickListener {

            val updatedBirthday = editedBirthday.birthday.copy(
                name = binding.birthdayNameEditText.text.toString(),
                birthdayDate = binding.birthdayDateEditText.text.toString(),
                note = binding.birthdayNoteEditText.text.toString()
            )

            guestBirthdayViewModel.updateBirthday(updatedBirthday)

            GuestFrequentlyUsedFunctions.loadAndStateOperation(
                viewLifecycleOwner,
                guestBirthdayViewModel,
                binding.threePointAnimation,
                binding.root,
                findNavController(),
                R.id.guestEditToMain
            )

        }

        binding.birthdayDateEditText.setOnClickListener {
            GuestFrequentlyUsedFunctions.showDatePickerDialog(requireContext(),binding.birthdayDateEditText)
        }

        binding.fabBackButton.setOnClickListener {
            findNavController().popBackStack()

        }

        return binding.root
    }

}