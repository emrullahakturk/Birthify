package com.yargisoft.birthify.ui.views.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthBirthdayDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class BirthdayDetailFragment : Fragment() {

    private var _binding: FragmentAuthBirthdayDetailBinding? = null
    private val binding get() = _binding!!

    private val detailedBirthday: BirthdayDetailFragmentArgs by navArgs()

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBirthdayDetailBinding.inflate(inflater, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.birthdayDetailFragment, true)
            .build()

        binding.birthdayNameEditText.setText(detailedBirthday.birthday.name)
        binding.birthdayDateEditText.setText(detailedBirthday.birthday.birthdayDate)
        binding.notifyTimeEditText.setText(detailedBirthday.birthday.notifyDate)
        binding.birthdayNoteEditText.setText(detailedBirthday.birthday.note)


        binding.fabBackButtonDetail.setOnClickListener { findNavController().popBackStack() }

        binding.birthdayEditButton.setOnClickListener {
            val action = BirthdayDetailFragmentDirections.detailToEdit(
                detailedBirthday.birthday
            )
            findNavController().navigate(action, navOptions)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
