package com.yargisoft.birthify.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentForgotPasswordBinding
import com.yargisoft.birthify.viewmodels.AuthViewModel


class ForgotPasswordFragment : Fragment() {
     private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this  ).get(AuthViewModel::class.java)

        // Inflate the layout for this fragment
        binding=  DataBindingUtil.inflate(inflater,R.layout.fragment_forgot_password, container, false)
        binding.fabForgotPassword.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.forgotPassSignInTv.setOnClickListener { it.findNavController().navigate(R.id.forgotToLogin) }

        binding.resetPassButton.setOnClickListener {
            val email = binding.resetPassEmailTv.text.toString()
            viewModel.resetPassword(email)


            viewModel.resetPasswordState.observe(viewLifecycleOwner, Observer { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Password reset failed", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return binding.root

    }

}