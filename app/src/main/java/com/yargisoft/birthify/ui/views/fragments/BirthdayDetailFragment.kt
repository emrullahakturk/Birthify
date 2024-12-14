package com.yargisoft.birthify.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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

    private lateinit var binding : FragmentAuthBirthdayDetailBinding

    private val detailedBirthday : BirthdayDetailFragmentArgs by navArgs()

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_auth_birthday_detail, container, false)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.birthdayDetailFragment, true)
            .build()

        binding.birthday = detailedBirthday.birthday
        binding.fabBackButtonDetail.setOnClickListener { findNavController().popBackStack() }


        binding.birthdayEditButton.setOnClickListener {
            val action = BirthdayDetailFragmentDirections.detailToEdit(
                detailedBirthday.birthday
            )
            findNavController().navigate(action, navOptions)
        }
        return binding.root
    }
}