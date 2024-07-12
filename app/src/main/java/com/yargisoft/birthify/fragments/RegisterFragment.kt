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
import com.yargisoft.birthify.databinding.FragmentRegisterBinding

import com.google.firebase.auth.FirebaseAuth
import com.yargisoft.birthify.viewmodels.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var viewModel : AuthViewModel
    val auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register, container, false)
        binding.signInRegisterTv.setOnClickListener {it.findNavController().navigate(R.id.registerToLogin)}
        binding.fabRegister.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.forgotPassRegisterTv.setOnClickListener { it.findNavController().navigate(R.id.registerToForgot) }

        binding.registerButton.setOnClickListener {
           val name = binding.registerFullNameTv.text.toString()
           val email = binding.registerEmailTv.text.toString()
           val password = binding.registerPassTv.text.toString()
           viewModel.registerUser(name,email,password)

        }

        viewModel.registrationState.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {

            } else {
                Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        })


        return binding.root
    }
}