package com.yargisoft.birthify.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentForgotPasswordBinding


class ForgotPasswordFragment : Fragment() {
     private lateinit var binding: FragmentForgotPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=  DataBindingUtil.inflate(inflater,R.layout.fragment_forgot_password, container, false)
        binding.fabForgotPassword.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.forgotPassSignInTv.setOnClickListener { it.findNavController().navigate(R.id.forgotToLogin) }

        return binding.root

    }

}