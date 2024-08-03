package com.yargisoft.birthify.views.guest_user_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentGuestBirthdayDetailBinding

class GuestBirthdayDetailFragment : Fragment() {

    private lateinit var binding : FragmentGuestBirthdayDetailBinding
    private val detailedBirthday : GuestBirthdayDetailFragmentArgs by navArgs()

    val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.guestBirthdayDetailFragment, true)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_birthday_detail, container, false)


        binding.birthday = detailedBirthday.birthday
        binding.fabBackButton.setOnClickListener { findNavController().popBackStack() }
        binding.birthdayEditButton.setOnClickListener {
            val action = GuestBirthdayDetailFragmentDirections.guestDetailToEdit(
                detailedBirthday.birthday
            )
            findNavController().navigate(action,navOptions)
        }


        return binding.root
    }




}