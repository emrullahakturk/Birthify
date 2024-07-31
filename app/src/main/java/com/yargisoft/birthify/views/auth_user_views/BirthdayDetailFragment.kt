package com.yargisoft.birthify.views.auth_user_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentAuthBirthdayDetailBinding

class BirthdayDetailFragment : Fragment() {

    private lateinit var binding : FragmentAuthBirthdayDetailBinding
    private val detailedBirthday : BirthdayDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_auth_birthday_detail, container, false)

        binding.birthday = detailedBirthday.birthday
        binding.fabBackButton.setOnClickListener { findNavController().popBackStack() }
        binding.birthdayEditButton.setOnClickListener {
            val action = BirthdayDetailFragmentDirections.detailToEdit(
                detailedBirthday.birthday
            )
            findNavController().navigate(action)
        }

        // Inflate the layout for this fragment
        return binding.root
    }



}